package org.anuhisoc.collectous.entry.setup


import android.accounts.Account
import android.app.Application
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.anuhisoc.collectous.R
import timber.log.Timber
import java.util.*


class WaitingPermissionViewModel(application: Application) : AndroidViewModel(application) {


    init {
        viewModelScope.launch {
            /*Temporary: Just so to show there exist a drive compatibility screen;*/
            Timber.d("init")
/*            delay(4000)*/
            Timber.d("val changed")
        }
    }



    private val _isPermissionGranted = MutableLiveData<Boolean>()
    val isPermissionGranted: LiveData<Boolean> = _isPermissionGranted

    private val account: Account?
        get() = GoogleSignIn.getLastSignedInAccount(getApplication<Application>().applicationContext)?.account


    private val credential = GoogleAccountCredential.usingOAuth2(getApplication<Application>().applicationContext, Collections.singleton(Scopes.DRIVE_FILE)).apply {
        selectedAccount = account
    }

    private val appName = getApplication<Application>().resources.getString(R.string.app_name)

    private val drive: Drive = Drive.Builder(AndroidHttp.newCompatibleTransport(), AndroidJsonFactory(), credential)
            .setApplicationName(appName)
            .build()

    fun checkCompatibility(emailAddress: String) {
        if(emailAddress.isEmpty()){
            _isPermissionGranted.value = false }
        else{
            account?.let { account->
                credential.selectedAccount = account
                Timber.d(" $account")
                Timber.d(GoogleSignIn.getLastSignedInAccount(getApplication<Application>().applicationContext)?.displayName)
                val job =  viewModelScope.launch {
                    withContext(Dispatchers.IO){

                        /*Temporary*/
                        val fileMetadata = File()
                        fileMetadata.name = "household"
                        fileMetadata.mimeType = "application/vnd.google-apps.spreadsheet"

                        val file: File = drive.files().create(fileMetadata)
                                .setFields("id")
                                .execute()

                        Timber.d(file.toPrettyString())
                        val permission = Permission()
                                .setType("user")
                                .setRole("writer")
                                .setEmailAddress("backupzkalai@gmail.com")


                        drive.permissions().create(file.id,permission)
                                .setFields("id").execute()

                        Timber.d("Drive file uploaded")
                    }
                }
                job.invokeOnCompletion { _isPermissionGranted.value = true }
            }
        }
    }




}