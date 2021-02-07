package org.anuhisoc.collectous.entry

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.FragmentResultListener
import org.anuhisoc.collectous.MainActivity
import org.anuhisoc.collectous.R
import timber.log.Timber
import timber.log.Timber.DebugTree


/**
 * This class is in Kotlin as DataStore is based on Kotlin. https://developer.android.com/topic/libraries/architecture/datastore#kotlin
 * We could use java, but we need to use RxJava for it.*/

class EntryActivity : AppCompatActivity() {

    companion object{
        const val REQ_KEY_FRAGMENT_MAIN_LAUNCH="fragment_request_key_main"
        const val RESULT_MAIN_LAUNCH="result_main_launch"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        if(savedInstanceState==null) {
            Timber.plant(DebugTree())
            Timber.d("Launching Entry Activity")
        }


        supportFragmentManager.setFragmentResultListener(REQ_KEY_FRAGMENT_MAIN_LAUNCH,this, FragmentResultListener { _, result ->
            Timber.d("Result listener $result")
            if(result.getBoolean(RESULT_MAIN_LAUNCH)){
                launchMainActivity()
            }
        })

    }

    private fun launchMainActivity(){
        val animBundle = ActivityOptionsCompat.makeCustomAnimation(applicationContext,R.anim.fade_in,R.anim.fade_out).toBundle()
        startActivity(Intent(applicationContext,MainActivity::class.java),animBundle)
        finish()
    }




}