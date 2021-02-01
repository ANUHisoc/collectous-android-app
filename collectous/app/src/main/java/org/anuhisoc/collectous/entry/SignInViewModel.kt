package org.anuhisoc.collectous.entry

import android.app.Application
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.anuhisoc.collectous.R
import org.anuhisoc.collectous.store
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SignInViewModel(application: Application) : AndroidViewModel(application) {


    private val dataStore: DataStore<Preferences> = getApplication<Application>()
            .let{ app->
                app.createDataStore(app.applicationContext.getString(R.string.data_store_settings)) }

    private val profileNameKey
        get() = getApplication<Application>().getString(R.string.data_store_key_profile_name)

    private val profileEmailKey
        get() = getApplication<Application>().getString(R.string.data_store_key_profile_email)

    private val profilePictureFileName
        get() = getApplication<Application>().getString(R.string.filename_profile_picture)

    private lateinit var account:GoogleSignInAccount

    val accountName:String
        get() = if(::account.isInitialized) account.displayName ?: "" else ""

    private val profilePictureOutputStream =
            FileOutputStream(File(getApplication<Application>().filesDir, profilePictureFileName))

    private val basicSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()

    val googleSignInClient:GoogleSignInClient
            by lazy { GoogleSignIn.getClient(getApplication<Application>().applicationContext, basicSignInOption) }


    /*Returns true if successful*/
    suspend fun updateGoogleAccount(account: GoogleSignInAccount?):Boolean = suspendCoroutine{ cont->
        if (account != null) {
            this.account = account
            val cacheAccountInfoJob = viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    cacheProfilePicture(account, profilePictureOutputStream)
                    cacheName(account)
                    cacheEmail(account)
                }
            }
            cacheAccountInfoJob.invokeOnCompletion { cause ->
                Timber.d("job $cause")
                if(cause==null){
                    cont.resume(true)
                    Timber.d("Updated Google Account")
                }
                else{
                    signOut()
                    cont.resume(false)
                }
            }
        }
    }


    private fun signOut(){
        googleSignInClient.signOut()
    }


    private suspend fun cacheName(account: GoogleSignInAccount){
        account.displayName?.let { name->
            val profileNamePrefKey = stringPreferencesKey(profileNameKey)
            dataStore.store(profileNamePrefKey, name)
        }
    }


    private suspend fun cacheEmail(account: GoogleSignInAccount){
        account.email?.let { email->
            val emailPrefKey = stringPreferencesKey(profileEmailKey)
            dataStore.store(emailPrefKey, email)
        }
    }


    /*TODO we need to query for space before saving it to app specific internal storage*/
    private suspend fun cacheProfilePicture(account: GoogleSignInAccount, fileOutputStream: FileOutputStream?) {
        Timber.d("Downloading and Saving profile picture")
        val profilePictureURL = account.photoUrl?.toString()
        val profilePicture = profilePictureURL?.let { url ->
            viewModelScope.async {
                Timber.d("URL is $url")
                downloadProfilePicture(url)
            }
        }
        profilePicture?.await()?.let { pic-> saveProfilePicture(pic, fileOutputStream) }
        Timber.d("Is File Successfully saved? ${File(getApplication<Application>().filesDir, profilePictureFileName).exists()}")
    }


    private fun saveProfilePicture(profilePicture: Bitmap, fileOutputStream: FileOutputStream?) {
        fileOutputStream.use { outputStream ->
            profilePicture.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        }
    }


    private suspend fun downloadProfilePicture(url: String): Bitmap? = suspendCoroutine { cont ->
        val requestQueue = Volley.newRequestQueue(getApplication<Application>().applicationContext)
        val downloadListener = Response.Listener<Bitmap> { bitmap ->  cont.resume(bitmap)}
        val errorListener = Response.ErrorListener { Timber.e("Error while downloading profile picture")
            cont.resume(null)}
        val imageRequest = ImageRequest(url, downloadListener, 0, 0, ImageView.ScaleType.FIT_XY, Bitmap.Config.ARGB_8888, errorListener)
        requestQueue.add(imageRequest)
    }

}