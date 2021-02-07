package org.anuhisoc.collectous.entry

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.net.*
import android.widget.ImageView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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

    private val profileNameKey = stringPreferencesKey(getApplication<Application>().getString(R.string.data_store_key_profile_name))

    private val profileEmailKey = stringPreferencesKey(getApplication<Application>().getString(R.string.data_store_key_profile_email))

    private val profilePictureFileName = getApplication<Application>().getString(R.string.filename_profile_picture)

    private lateinit var account:GoogleSignInAccount

    private lateinit var networkCallback:ConnectivityManager.NetworkCallback

    val accountName:String
        get() = if(::account.isInitialized) account.displayName ?: "" else ""

    private val profilePictureOutputStream by lazy{
            FileOutputStream(File(getApplication<Application>().filesDir, profilePictureFileName))}

    private val basicSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .build()

    val googleSignInClient:GoogleSignInClient
            by lazy { GoogleSignIn.getClient(getApplication<Application>().applicationContext, basicSignInOption) }

    private val _isSuccessSnackBarDismissed = MutableLiveData<Boolean>()
    val isSuccessSnackBarDismissed: LiveData<Boolean> = _isSuccessSnackBarDismissed

    private val connectionManager by lazy { getApplication<Application>()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }


    /*Returns true if successful*/
    suspend fun updateGoogleAccount(account: GoogleSignInAccount?):Boolean = suspendCoroutine{ cont->
        viewModelScope.launch {
            if(!isOnline())
                cont.resume(false)
            if(this@SignInViewModel::networkCallback.isInitialized)
                connectionManager.unregisterNetworkCallback(networkCallback)
        }

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
            dataStore.store(profileNameKey, name)
        }
    }


    private suspend fun cacheEmail(account: GoogleSignInAccount){
        account.email?.let { email->
            dataStore.store(profileEmailKey, email)
        }
    }

    fun setSnackBarDismissed(){
        _isSuccessSnackBarDismissed.value = true
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


    private suspend fun isOnline(): Boolean  = suspendCoroutine { cont->
        val networkRequest = NetworkRequest.Builder().addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
        networkCallback = object: ConnectivityManager.NetworkCallback(){
            override fun onAvailable(network: Network) { super.onAvailable(network)
                cont.resume(true) }
            override fun onUnavailable() { super.onUnavailable()
                cont.resume(false)
            }
            override fun onLosing(network: Network, maxMsToLive: Int) { super.onLosing(network, maxMsToLive)
                cont.resume(false)
            }
        }
        connectionManager.registerNetworkCallback(networkRequest,networkCallback)

    }
}