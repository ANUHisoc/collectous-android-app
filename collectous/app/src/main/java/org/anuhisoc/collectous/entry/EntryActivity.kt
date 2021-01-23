package org.anuhisoc.collectous.entry

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.createDataStore
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.anuhisoc.collectous.MainActivity
import org.anuhisoc.collectous.R
import org.anuhisoc.collectous.databinding.ActivityEntryBinding
import timber.log.Timber
import timber.log.Timber.DebugTree


/* This class is in Kotlin as DataStore is based on Kotlin.https://developer.android.com/topic/libraries/architecture/datastore#kotlin
* We could use java, but we need to use RxJava for it. Sidenote: I am not familiar with RxJava and am planning to learn it.*/

class EntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEntryBinding
    private lateinit var entryViewModel:EntryViewModel

    private  var account: GoogleSignInAccount? = null

    companion object{
        private const val ON_BOARDING_KEY = "isOnBoardingCompletedKey" }

    private val isAlreadySignedIn: Boolean
        get() {
            account = GoogleSignIn.getLastSignedInAccount(this)
            return account != null }

    private val dataStore: DataStore<Preferences>?
        get() = applicationContext?.createDataStore("settings")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.plant(DebugTree())
        entryViewModel = ViewModelProvider(this).get(EntryViewModel::class.java)

        lifecycleScope.launch{
            if(isOnBoardingCompleted())
                setOnBoardingCompleted()
            /*TODO launch sign in process only after on-boarding process*/

            if (!isAlreadySignedIn) {
                /*Launching google sign in process*/
                Timber.d("User not signed in yet")
                if (savedInstanceState == null) {
                    launchFragment(SignInFragment())
                }
            }
        }

        entryViewModel.accountLiveData.observe(this, Observer { account: GoogleSignInAccount? ->
            account?.let {
                /*ViewModel will be updated on the account details via SignInFragment; Need to remove Fragment once done;
                 Account null if sign in process failed*/
                val signInFragment = supportFragmentManager.findFragmentByTag(SignInFragment::class.java.simpleName)
                if (signInFragment != null) {
                    removeFragment(signInFragment)
                    startMainActivity()
                }
            }
        })
    }


    private fun startMainActivity(){
        val mainActivityIntent = Intent(this,MainActivity::class.java)
        startActivity(mainActivityIntent,null)
        finish()
    }

    private suspend fun isOnBoardingCompleted():Boolean {
        val isOnBoardingKey =  booleanPreferencesKey(ON_BOARDING_KEY)
        val isOnBoardFlow = dataStore?.data?.map { preferences -> preferences[isOnBoardingKey] ?: true }
        return isOnBoardFlow?.first()?:true
    }

    private fun removeFragment(fragment: Fragment) {
        supportFragmentManager
                .beginTransaction()
                .remove(fragment)
                .commit()
    }

    /* Custom method to launch fragments; Assumes that fragments are not anonymous class*/
    private fun launchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.entry_fragment_container_view, fragment, fragment.javaClass.simpleName)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit()
    }


    private suspend fun setOnBoardingCompleted(){
        dataStore?.edit { settings ->
            val isOnBoardingCompletedKey =  booleanPreferencesKey(ON_BOARDING_KEY)
            settings[isOnBoardingCompletedKey] =  true
        }
    }


}