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

    private val dataStore: DataStore<Preferences>
        get() = getApplication<Application>()
                .let{ app->
                    app.createDataStore(app.applicationContext.getString(R.string.data_store_settings)) }

    val isOnBoardingCompleted
        get() = viewModelScope.async { isOnBoardingCompleted() }


    private val onBoardingKey
        get() = getApplication<Application>().getString(R.string.data_store_key_on_boarding)




    private suspend fun isOnBoardingCompleted(): Boolean {
        val isOnBoardingKey = booleanPreferencesKey(onBoardingKey)
        val isOnBoardingCompletedFlow = dataStore.data.map { preferences ->
            preferences[isOnBoardingKey] ?: true
        }
        /*TODO temporarily setting it to true since on-boarding process is yet to be implemented*/
        return isOnBoardingCompletedFlow.first()
    }


    private suspend fun setOnBoardingCompleted() {
        dataStore.edit { settings ->
            val isOnBoardingCompletedKey = booleanPreferencesKey(onBoardingKey)
            settings[isOnBoardingCompletedKey] = true
        }
    }


}