package org.anuhisoc.collectous.entry

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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

        if(savedInstanceState==null) {
            Timber.plant(DebugTree())
            Timber.d("Launching Entry Activity")
            val navHostFragment =
                    supportFragmentManager.findFragmentById(R.id.entry_nav_host_fragment) as NavHostFragment
            val navController = navHostFragment.navController

            lifecycleScope.launch {
                /*Temporary: Just so to show there exist a splash screen*/
                delay(3000)

                val isOnBoardingCompleted = entryViewModel.isOnBoardingCompleted.await()
                /*TODO we need to link onBoarding later*/
                if(!entryViewModel.isSignInProcessCompleted){
                    navController.navigate(R.id.initialisation)
                }

            }
        }



    }




}