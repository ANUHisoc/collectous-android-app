package org.anuhisoc.collectous.entry

import android.app.Application
import android.graphics.Bitmap
import android.widget.ImageView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.anuhisoc.collectous.R
import org.anuhisoc.collectous.store
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * This class is in Kotlin as DataStore is based on Kotlin.https://developer.android.com/topic/libraries/architecture/datastore#kotlin
 * We could use java, but we need to use RxJava for it.*/

class EntryViewModel(application: Application) : AndroidViewModel(application) {

    private var _accountLiveData: MutableLiveData<GoogleSignInAccount> = MutableLiveData()
    val accountLiveData: LiveData<GoogleSignInAccount> get() = _accountLiveData

    private val isAlreadySignedIn: Boolean
        get() = GoogleSignIn.getLastSignedInAccount(getApplication<Application>().applicationContext)!=null


    private var isDataSuccessfullyCached: Boolean = false

    val isSignInProcessCompleted:Boolean
        get() = isAlreadySignedIn && isDataSuccessfullyCached

    private val dataStore: DataStore<Preferences>?
        get() = getApplication<Application>()
                .let{ app->
                    app.createDataStore(app.applicationContext.getString(R.string.data_store_settings)) }

    val isOnBoardingCompleted
        get() = viewModelScope.async { isOnBoardingCompleted() }

    private val profilePictureFileName
        get() = getApplication<Application>().getString(R.string.filename_profile_picture)

    private val onBoardingKey
        get() = getApplication<Application>().getString(R.string.data_store_key_on_boarding)

    private val profileNameKey
        get() = getApplication<Application>().getString(R.string.data_store_key_profile_name)

    private val profileEmailKey
        get() = getApplication<Application>().getString(R.string.data_store_key_profile_email)


    fun updateGoogleAccount(account: GoogleSignInAccount?) {
        if (account != null) {
            val profilePictureOutputStream = FileOutputStream(File(getApplication<Application>().filesDir, profilePictureFileName))
            val cacheAccountInfoJob = viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    cacheProfilePicture(account,profilePictureOutputStream)
                    cacheName(account)
                    cacheEmail(account)
                }
            }
            cacheAccountInfoJob.invokeOnCompletion { cause ->   Timber.d("job $cause")
                if(cause==null){
                    isDataSuccessfullyCached = true
                    _accountLiveData.value = account
                    Timber.d("Updated Google Account")
                } }

        }
    }


    private suspend fun cacheName(account: GoogleSignInAccount){
        account.displayName?.let {name->
            val profileNamePrefKey = stringPreferencesKey(profileNameKey)
            dataStore?.store(profileNamePrefKey,name) }
    }


    private suspend fun cacheEmail(account: GoogleSignInAccount){
        account.email?.let { email->
            val emailPrefKey = stringPreferencesKey(profileEmailKey)
            dataStore?.store(emailPrefKey,email) }
    }


    private suspend fun isOnBoardingCompleted(): Boolean {
        val isOnBoardingKey = booleanPreferencesKey(onBoardingKey)
        val isOnBoardingCompletedFlow = dataStore?.data?.map { preferences ->
            preferences[isOnBoardingKey] ?: true
        }
        /*TODO temporarily setting it to true since on-boarding process is yet to be implemented*/
        return isOnBoardingCompletedFlow?.first() ?: true
    }


    private suspend fun setOnBoardingCompleted() {
        dataStore?.edit { settings ->
            val isOnBoardingCompletedKey = booleanPreferencesKey(onBoardingKey)
            settings[isOnBoardingCompletedKey] = true
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
        val requestQueue = newRequestQueue(getApplication<Application>().applicationContext)
        val downloadListener = Response.Listener<Bitmap> { bitmap ->  cont.resume(bitmap)}
        val errorListener = Response.ErrorListener { Timber.e("Error while downloading profile picture")
            cont.resume(null)}
        val imageRequest = ImageRequest(url,downloadListener ,0,0, ImageView.ScaleType.FIT_XY,Bitmap.Config.ARGB_8888,errorListener)
        requestQueue.add(imageRequest)
    }


}