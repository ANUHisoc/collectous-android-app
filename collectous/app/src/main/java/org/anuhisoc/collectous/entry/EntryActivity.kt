package org.anuhisoc.collectous.entry

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.launch
import org.anuhisoc.collectous.MainActivity
import org.anuhisoc.collectous.R
import timber.log.Timber
import timber.log.Timber.DebugTree


/**
 * This class is in Kotlin as DataStore is based on Kotlin. https://developer.android.com/topic/libraries/architecture/datastore#kotlin
 * We could use java, but we need to use RxJava for it.*/

class EntryActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)
        val entryViewModel:EntryViewModel by viewModels()

      /*TODO need to replace with a better way of navigation; For now this looks fine but this way does not considers the "extensibility" aspect */
        if(savedInstanceState==null) {
            Timber.plant(DebugTree())
            Timber.d("Launching loading Activity")
            launchFragment(LoadingFragment(), savedInstanceState)
            lifecycleScope.launch {
                if (entryViewModel.isOnBoardingCompleted.await()) {
                    if (!entryViewModel.isSignInProcessCompleted ) {
                        Timber.d("Launching SignIn fragment")
                        launchFragment(SignInFragment(), savedInstanceState)
                    } else {
                        Timber.d("Launching MainActivity")
                        startMainActivity()
                    }
                } else {
                    /*TODO launch on-boarding process and then launch sign in process only after on-boarding process*/
                }
            }
        }
        entryViewModel.accountLiveData.observe(this, Observer { account: GoogleSignInAccount? ->
            account?.let {
                /*ViewModel will be updated on the account details via SignInFragment; Need to remove Fragment once done;
             Account will be null if sign in process failed*/
                startMainActivity()
            }
        })

    }


    override fun onResume() {
        super.onResume()
        Timber.d("onResume")
    }

    private fun startMainActivity(){
        val mainActivityIntent = Intent(this,MainActivity::class.java)
        startActivity(mainActivityIntent,null)
        finish()
    }


    /*Custom method to launch fragments;
    Checks for any savedInstanceState - to prevent fragment being created again upon device rotation*/
    private fun launchFragment(fragment: Fragment,savedInstanceState: Bundle?) {
        Timber.d("Launching ${fragment.javaClass.simpleName} successful? ${savedInstanceState==null}")
        if(savedInstanceState==null)
            supportFragmentManager.beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.entry_fragment_container_view, fragment, fragment.javaClass.simpleName)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit()
    }

}