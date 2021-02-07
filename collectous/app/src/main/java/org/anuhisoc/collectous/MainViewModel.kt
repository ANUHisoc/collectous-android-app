package org.anuhisoc.collectous

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MainViewModel(application: Application): AndroidViewModel(application) {
    private val dataStore: DataStore<Preferences> = getApplication<Application>()
            .let{ app->
                app.createDataStore(app.applicationContext.getString(R.string.data_store_settings)) }

    private val profileNameKey = stringPreferencesKey(getApplication<Application>().getString(R.string.data_store_key_profile_name))


    val name = viewModelScope.async{ getProfileName() }


    private suspend fun getProfileName(): String {
        val profileNameFlow = dataStore.data.map { preferences ->
            preferences[profileNameKey] ?: ""
        }
        return profileNameFlow.first()
    }
}