package org.anuhisoc.collectous.entry.permission


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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.anuhisoc.collectous.R
import timber.log.Timber
import java.util.*


class SheetCompatibilityViewModel(application: Application) : AndroidViewModel(application) {


    init {
        viewModelScope.launch {
            /*Temporary: Just so to show there exist a drive compatibility screen;*/
            Timber.d("init")
            delay(4000)
            Timber.d("val changed")
        }
    }

    fun checkCompatibility(link: String) {
        if(link.isEmpty()){
            _isDriveCompatible.value = false
        }
        else{
            account?.let { account->
            credential.selectedAccount = account
            Timber.d(" $account")
               Timber.d(GoogleSignIn.getLastSignedInAccount(getApplication<Application>().applicationContext)?.displayName)
                viewModelScope.launch {
                    withContext(Dispatchers.IO){


                        val fileMetadata = File()
                        fileMetadata.name = "TestCollectous6"
                        fileMetadata.mimeType = "application/vnd.google-apps.folder"

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
/*
                        drive.permissions().create(file.id, permission)
                                .setFields("id")
                                .execute()*/

                /*        val file = File().setName("TestCollectous")
                      *//*  val permission = Permission()
                                .setId(file?.id)
                                .setType("user")
                                .setEmailAddress("backupzkalai@gmail.com")
                                .setRole("fileOrganizer")*//*
                   *//*     file.permissions = listOf(permission)*//*
                        drive.files().create(file).execute()*/



                   Timber.d("Drive file uploaded")
                    }
                }
            }
        }
    }


    private val account: Account?
        get() = GoogleSignIn.getLastSignedInAccount(getApplication<Application>().applicationContext)?.account


    private val _isDriveCompatible = MutableLiveData<Boolean>()
    val isDriveCompatible: LiveData<Boolean> = _isDriveCompatible


   private val credential = GoogleAccountCredential.usingOAuth2(getApplication<Application>().applicationContext, Collections.singleton(Scopes.DRIVE_FILE)).apply {
        selectedAccount = account
    }


    private val appName = getApplication<Application>().resources.getString(R.string.app_name)

    private val drive: Drive = Drive.Builder(AndroidHttp.newCompatibleTransport(), AndroidJsonFactory(), credential)
                .setApplicationName(appName)
                .build()

/*credential.setSelectedAccount(mAccount.getAccount());*/

}