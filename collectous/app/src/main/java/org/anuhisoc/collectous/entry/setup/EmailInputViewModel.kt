package org.anuhisoc.collectous.entry.setup

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.anuhisoc.collectous.R

class EmailInputViewModel(application: Application) : AndroidViewModel(application) {
    private val ngoAdminEmailKey
            = stringPreferencesKey(getApplication<Application>().getString(R.string.data_store_key_ngo_admin_email))

    private val dataStore: DataStore<Preferences> = getApplication<Application>()
        .let{ app->
            app.createDataStore(app.applicationContext.getString(R.string.data_store_settings)) }

    private val _ngoAdminEmail = MutableLiveData<String>()
    val ngoAdminEmail: LiveData<String> =_ngoAdminEmail

    init {
        viewModelScope.launch{
            _ngoAdminEmail.value = getNgoAdminEmail()
        }
    }


    private suspend fun getNgoAdminEmail():String=
        dataStore.data.map { preferences->
            preferences[ngoAdminEmailKey]?:""
        }.first()


}