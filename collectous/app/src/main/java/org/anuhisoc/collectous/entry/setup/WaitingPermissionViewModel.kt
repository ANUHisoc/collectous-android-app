package org.anuhisoc.collectous.entry.setup


import android.accounts.Account
import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.createDataStore
import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import com.google.api.services.drive.model.Permission
import kotlinx.coroutines.Dispatchers

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.anuhisoc.collectous.R
import timber.log.Timber
import java.util.*


class WaitingPermissionViewModel(application: Application) : AndroidViewModel(application) {


   companion object{ private const val CHECK_FREQUENCY = 10000L }

    private val rootDriveIdKey
            = stringPreferencesKey(getApplication<Application>().getString(R.string.data_store_key_root_drive_id))

    private val ngoAdminEmailKey
            = stringPreferencesKey(getApplication<Application>().getString(R.string.data_store_key_ngo_admin_email))

    private val dataStore: DataStore<Preferences> = getApplication<Application>()
        .let{ app->
            app.createDataStore(app.applicationContext.getString(R.string.data_store_settings)) }

    private val isInjectedFilesCompatible = booleanPreferencesKey(getApplication<Application>()
        .getString(R.string.data_store_key_injected_file_compatible))

    private val _isPermissionGranted = MutableLiveData<Boolean>()
    val isPermissionGranted: LiveData<Boolean> = _isPermissionGranted

    private val account: Account?
        get() = GoogleSignIn.getLastSignedInAccount(getApplication<Application>().applicationContext)?.account


    private val credential = GoogleAccountCredential.usingOAuth2(
        getApplication<Application>().applicationContext,
        Collections.singleton(Scopes.DRIVE_FILE)
    ).apply {
        selectedAccount = account
    }

    private val appName = getApplication<Application>().resources.getString(R.string.app_name)

    private val drive: Drive =
        Drive.Builder(AndroidHttp.newCompatibleTransport(), AndroidJsonFactory(), credential)
            .setApplicationName(appName)
            .build()


    private suspend fun createDriveFolder(emailAddress: String) {
        account?.let { account ->
            credential.selectedAccount = account
            Timber.d(" $account")
            Timber.d(GoogleSignIn.getLastSignedInAccount(getApplication<Application>().applicationContext)?.displayName)


            /*See false positive warning issue
            https://youtrack.jetbrains.com/issue/KTIJ-838*/

            val fileMetadata = File()
            fileMetadata.name = "Collectous"
            fileMetadata.mimeType = "application/vnd.google-apps.folder"

            val file: File = drive.files()
                .create(fileMetadata)
                .setFields("id")
                .execute()

            Timber.d(file.toPrettyString())

            val permission = Permission()
                .setType("user")
                .setRole("writer")
                .setEmailAddress(emailAddress)


            drive.permissions().create(file.id, permission)
                .setFields("id").execute()

            dataStore.edit { settings  ->
                settings[rootDriveIdKey] = file.id
            }

            Timber.d("Drive file uploaded")

        }
    }

    private suspend fun getNgoAdminEmail():String=
        dataStore.data.map { preferences->
            preferences[ngoAdminEmailKey]?:""
        }.first()



    private suspend fun getDriveFolderId():String=
        dataStore.data.map { preferences->
            preferences[rootDriveIdKey]?:""
        }.first()


    private suspend fun isDriveFolderCreated():Boolean =
        getDriveFolderId().isNotBlank()

    /*Simply checks if admin is the last onw to update the folder;
    TODO: Need a more stringent way of checking if files are injected
     Note: Currently injecting files does not modify the folder.
     */

    private suspend fun hasAdminInjectedFiles(folder:File):Boolean
            = folder.lastModifyingUser?.emailAddress?.let { getNgoAdminEmail().contentEquals(it) } ?: false



    fun checkCompatibilityFrequently(emailAddress: String){
        viewModelScope.launch(Dispatchers.IO) {
            while(true){
                checkCompatibility(emailAddress)
                delay(CHECK_FREQUENCY)
                Timber.d("Checking compatibility")
            }
        }


    }

    /**
     * Checks if folder is compatible to the app.*/
    private suspend fun checkCompatibility(emailAddress: String)  = withContext(Dispatchers.IO) {
        if (emailAddress.isEmpty()) {
            _isPermissionGranted.value = false
        } else {
            /*See false positive warning issue
             https://youtrack.jetbrains.com/issue/KTIJ-838*/


            if (!isDriveFolderCreated()
                || !getNgoAdminEmail().contentEquals(emailAddress)) {
                createDriveFolder(emailAddress)
                dataStore.edit { settings  ->
                    settings[ngoAdminEmailKey] = emailAddress
                }
            }

            // Folders are files
            val folder = drive.files().get(getDriveFolderId())
                .execute()

            Timber.d(folder.toPrettyString())
            val hasAdminInjectedFiles = hasAdminInjectedFiles(folder)
            dataStore.edit { settings ->
                settings[isInjectedFilesCompatible] = hasAdminInjectedFiles
            }
            withContext(Dispatchers.Main) {
                _isPermissionGranted.value = hasAdminInjectedFiles
            }

        }


    }
}





