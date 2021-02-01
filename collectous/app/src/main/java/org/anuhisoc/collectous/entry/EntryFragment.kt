package org.anuhisoc.collectous.entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import org.anuhisoc.collectous.R

class EntryFragment : Fragment() {
    private val entryViewModel:EntryViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            entryViewModel.isSplashScreenOverLiveData.observe(this, Observer { isSplashScreenOver->
                if(isSplashScreenOver){
                    lifecycleScope.launch {
                        val isSignInProcessCompleted = entryViewModel.isSignInProcessCompleted.await()

                        /*val isOnBoardingCompleted = entryViewModel.isOnBoardingCompleted.await()*/
                        /*TODO we need to link onBoarding later*/

                        if (!isSignInProcessCompleted) {
                            findNavController().navigate(R.id.action_entryFragment_to_initialisation)
                        } else {
                            requestToLaunchMainActivity()
                        }
                    }
                }
            })
    }


    private fun requestToLaunchMainActivity() {
        val bundle = bundleOf(EntryActivity.RESULT_MAIN_LAUNCH to true)
        requireActivity().supportFragmentManager.setFragmentResult(EntryActivity.REQ_KEY_FRAGMENT_MAIN_LAUNCH,bundle)
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_entry, container, false)
    }
}