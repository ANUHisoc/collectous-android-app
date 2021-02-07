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
import java.util.*

/*TODO: Need to consider encapsulating access to sheets and drive,building of layout based on result etc later on */
class AddHouseholdViewModel(application: Application): AndroidViewModel(application) {

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

    private val householdSheetID by lazy { drive.files().list().setQ("name='household'").execute().files?.get(0)?.id }

    private val range = "A0:A7"

    var response: ValueRange = sheets.spreadsheets().values()
            .get(householdSheetID, range)
            .execute()

}