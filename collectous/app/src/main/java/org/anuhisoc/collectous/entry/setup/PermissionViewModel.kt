package org.anuhisoc.collectous.entry.setup

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope

class PermissionViewModel(application: Application) : AndroidViewModel(application){

    private val appDriveScope = Scope(Scopes.DRIVE_FILE)


    private val driveSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(appDriveScope)
            .build()

    val googleSignInClient: GoogleSignInClient
            by lazy { GoogleSignIn.getClient(getApplication<Application>().applicationContext, driveSignInOption) }

    private val account
        get() = GoogleSignIn.getLastSignedInAccount(getApplication<Application>().applicationContext)

    val hasDrivePermission
        get() = GoogleSignIn.hasPermissions(account,appDriveScope)

}