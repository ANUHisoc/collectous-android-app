package org.anuhisoc.collectous.entry

import android.app.Application
import android.content.Context
import androidx.core.graphics.drawable.toIcon
import androidx.core.net.toFile
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File


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
        /*val profilePicture = account.photoUrl?.toFile()?.toURI()?.let { File(it) }
        getApplication<Application>().applicationContext?.openFileOutput("user_profile_picture", Context.MODE_PRIVATE).use {
            fileOutputStream->
            fileOutputStream?.write(profilePicture?.readBytes())
        }*/

        Timber.d("Is File Successfully saved? ${File(getApplication<Application>().filesDir,"user_profile_picture").exists()}")

    }
}