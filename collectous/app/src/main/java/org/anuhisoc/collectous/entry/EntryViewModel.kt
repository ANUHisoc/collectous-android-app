package org.anuhisoc.collectous.entry

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.ImageView
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.volley.Response
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * This class is in Kotlin as DataStore is based on Kotlin.https://developer.android.com/topic/libraries/architecture/datastore#kotlin
 * We could use java, but we need to use RxJava for it. Sidenote: I am not familiar with RxJava and am planning to learn it.*/

class EntryViewModel(application: Application) : AndroidViewModel(application) {

    private var _accountLiveData: MutableLiveData<GoogleSignInAccount> = MutableLiveData()
    val accountLiveData:LiveData<GoogleSignInAccount> get() = _accountLiveData

    private val dataStore: DataStore<Preferences>?
        get() = getApplication<Application>().createDataStore("settings")

    val isOnBoardingCompleted
        get() = viewModelScope.async { isOnBoardingCompleted() }

    companion object{
        const val ON_BOARDING_KEY = "isOnBoardingCompletedKey" }

    fun updateGoogleAccount(account: GoogleSignInAccount?) {
        if(account!=null){
            val job = viewModelScope.launch { downloadSaveProfilePicture(account) }
            job.invokeOnCompletion { _accountLiveData.value = account }
        }
    }

    private suspend fun isOnBoardingCompleted():Boolean {
        val isOnBoardingKey =  booleanPreferencesKey(ON_BOARDING_KEY)
        val isOnBoardingCompletedFlow = dataStore?.data?.map { preferences -> preferences[isOnBoardingKey] ?: true }
        /*TODO temporarily setting it to true since on-boarding process is yet to be implemented*/
        return isOnBoardingCompletedFlow?.first()?:true
    }


    private suspend fun setOnBoardingCompleted(){
        dataStore?.edit { settings ->
            val isOnBoardingCompletedKey =  booleanPreferencesKey(ON_BOARDING_KEY)
            settings[isOnBoardingCompletedKey] =  true
        }
    }


    /*TODO we need to query for space before saving it to app specific internal storage*/
    private fun downloadSaveProfilePicture(account: GoogleSignInAccount){
        Timber.d("Downloading and Saving profile picture")
        val profilePictureURL = account.photoUrl?.toString()
        val profilePicture =  profilePictureURL?.let {url ->
            viewModelScope.async{
                Timber.d("URL is $url")
                downloadProfilePicture(url)
            }
        }

        val fileOutputStream =  getApplication<Application>().applicationContext?.openFileOutput("img_user_profile_picture.jpg", Context.MODE_PRIVATE)
        viewModelScope.launch {
            profilePicture?.await()?.let { profilePicture->saveProfilePicture(profilePicture,fileOutputStream) }
        }

        Timber.d("Is File Successfully saved? ${File(getApplication<Application>().filesDir,"img_user_profile_picture.jpg").exists()}")
    }



    private fun saveProfilePicture(bitmap: Bitmap,fileOutputStream:FileOutputStream?){
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream)
    }

    private suspend fun downloadProfilePicture(url: String): Bitmap? = suspendCoroutine {
        cont->

 /*       Glide.with(getApplication<Application>().applicationContext)
                .asBitmap()
                .load(url)
                .listener(object : RequestListener<Bitmap?>{
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap?>?, isFirstResource: Boolean): Boolean {
                        cont.resume(null)
                        return true
                    }

                    override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        cont.resume(resource)
                        return true
                    }
                })*/

        val queue = newRequestQueue(getApplication<Application>().applicationContext)
        val downloadListener = Response.Listener<Bitmap> { bitmap ->  cont.resume(bitmap)}
        val errorListener = Response.ErrorListener { Timber.e("Error while downloading profile picture")
            cont.resume(null)}

        val imageRequest = ImageRequest(url,downloadListener ,96,96, ImageView.ScaleType.FIT_CENTER,Bitmap.Config.ARGB_8888,errorListener)
        queue.add(imageRequest)

/*        val client = OkHttpClient()

        val request: Request = Request.Builder()
                .url(url)
                .build()

      client.newCall(request).enqueue(object:Callback{
            override fun onFailure(call: Call, e: IOException) {
                cont.resume(null)
            }

            override fun onResponse(call: Call, response: Response) {
                val bitmap = BitmapFactory.decodeStream(response.body?.byteStream())

              cont.resume(bitmap)
            }
        })*/

    }

}