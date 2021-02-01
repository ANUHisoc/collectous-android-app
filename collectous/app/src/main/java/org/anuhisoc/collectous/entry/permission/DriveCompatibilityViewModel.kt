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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.anuhisoc.collectous.R
import timber.log.Timber
import java.io.ByteArrayOutputStream
import java.io.OutputStream
import java.util.*
import kotlin.concurrent.thread


class DriveCompatibilityViewModel(application: Application) : AndroidViewModel(application) {


    init {
        viewModelScope.launch {
            /*Temporary: Just so to show there exist a drive compatibility screen;*/
            Timber.d("init")
            delay(4000)
            _isDriveCompatible.value = true
            Timber.d("val changed")
        }
    }

    fun checkCompatibility() {
        account?.let { account->

            Timber.d(" $account")
            Timber.d(GoogleSignIn.getLastSignedInAccount(getApplication<Application>().applicationContext)?.displayName)
            Timber.d("Is drive files null? ${drive.files()==null}")
            thread {
                    val fileList =  drive.files().list().execute()
                    Timber.d("drive files list null? ${fileList}")
                    drive.files().list().map {
                        Timber.d("drivefile: $it")
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