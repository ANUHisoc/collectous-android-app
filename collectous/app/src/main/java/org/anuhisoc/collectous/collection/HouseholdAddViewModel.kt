package org.anuhisoc.collectous.collection

import android.accounts.Account
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.extensions.android.json.AndroidJsonFactory
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.drive.Drive
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import org.anuhisoc.collectous.R
import timber.log.Timber
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.thread
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/*TODO: Need to consider encapsulating access to sheets and drive,building of layout based on result etc later on */
class HouseholdAddViewModel(application: Application): AndroidViewModel(application) {

    private val account: Account?
        get() = GoogleSignIn.getLastSignedInAccount(getApplication<Application>().applicationContext)?.account


    private val credential = GoogleAccountCredential.usingOAuth2(getApplication<Application>().applicationContext, Collections.singleton(Scopes.DRIVE_FILE)).apply {
        selectedAccount = account
    }

    private val appName = getApplication<Application>().resources.getString(R.string.app_name)

    private val sheets: Sheets = Sheets.Builder(AndroidHttp.newCompatibleTransport(), AndroidJsonFactory(), credential)
            .setApplicationName(appName)
            .build()

    private val drive: Drive = Drive.Builder(AndroidHttp.newCompatibleTransport(), AndroidJsonFactory(), credential)
            .setApplicationName(appName)
            .build()

    private  val householdSheetID by lazy { drive.files().list().setQ("name='household'").execute().files?.get(0)?.id }

    /*Temporary:TODO need to consider for unknwon number of filled columns*/
    private val range = "A1:K1"


    private var executor: ExecutorService = Executors.newSingleThreadExecutor()

    suspend fun fetchHeader():ValueRange? =
            suspendCoroutine{ cont->
                thread{
                    try {
                        cont.resume(
                                executor.submit(Callable {
                                    Timber.d("executor invoked")
                                    sheets.spreadsheets().values()
                                            .get(householdSheetID, range)
                                            .execute()
                                }).get())
                    }
                    catch (e:Exception){
                        cont.resume(null)
                    }
                }
            }


    override fun onCleared() {
        super.onCleared()
        executor.shutdown()
    }
}