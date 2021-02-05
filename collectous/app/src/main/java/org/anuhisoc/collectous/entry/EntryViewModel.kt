package org.anuhisoc.collectous.entry

import android.app.Application
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.anuhisoc.collectous.R


/**
 * This class is in Kotlin as DataStore is based on Kotlin.https://developer.android.com/topic/libraries/architecture/datastore#kotlin
 * We could use java, but we need to use RxJava for it.*/

class EntryViewModel(application: Application) : AndroidViewModel(application) {

    private val isAlreadySignedIn: Boolean
        get() = account!=null

    private val account: GoogleSignInAccount?
        get() = GoogleSignIn.getLastSignedInAccount(getApplication<Application>().applicationContext)

    val isSignInProcessCompleted:Deferred<Boolean>
        get() = viewModelScope.async {isAlreadySignedIn && isDataSuccessfullyCached()}

    private val profileEmailKey = stringPreferencesKey(getApplication<Application>().getString(R.string.data_store_key_profile_email))

    private val dataStore: DataStore<Preferences> = getApplication<Application>()
            .let{ app->
                app.createDataStore(app.applicationContext.getString(R.string.data_store_settings)) }

    val isOnBoardingCompleted
        get() = viewModelScope.async { isOnBoardingCompleted() }

    private val isOnBoardingKey = booleanPreferencesKey(getApplication<Application>().getString(R.string.data_store_key_on_boarding))

    private val _isSplashScreenOverLiveData = MutableLiveData<Boolean>(false)
    val isSplashScreenOverLiveData:LiveData<Boolean> =_isSplashScreenOverLiveData

    init{
        viewModelScope.launch {
            /*Temporary: Making splashscreen visible for a while; We can replace it once we come up with a defined splashscreen.*/
            delay(2000)
            _isSplashScreenOverLiveData.value = true
        }
    }


    private suspend fun isDataSuccessfullyCached(): Boolean =
            dataStore.data.map { preferences ->
                preferences[profileEmailKey] ?: ""
            }.first().isNotBlank()

    
    private suspend fun isOnBoardingCompleted(): Boolean {
        val isOnBoardingCompletedFlow = dataStore.data.map { preferences ->
            preferences[isOnBoardingKey] ?: true
        }
        /*TODO temporarily setting it to true since on-boarding process is yet to be implemented*/
        return isOnBoardingCompletedFlow.first()
    }


    private suspend fun setOnBoardingCompleted() {
        dataStore.edit { settings ->
            settings[isOnBoardingKey] = true
        }
    }



}