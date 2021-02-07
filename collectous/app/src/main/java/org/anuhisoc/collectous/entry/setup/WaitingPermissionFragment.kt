package org.anuhisoc.collectous.entry.setup


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import org.anuhisoc.collectous.databinding.FragmentWaitingPermissionBinding
import org.anuhisoc.collectous.entry.EntryActivity
import timber.log.Timber


class WaitingPermissionFragment : Fragment() {


    private lateinit var binding: FragmentWaitingPermissionBinding
    private val waitingPermissionViewModel: WaitingPermissionViewModel by viewModels()
    private var emailAddress = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentWaitingPermissionBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        arguments?.let {
            emailAddress = it.getString("email").toString()
        }
        Timber.d("Email address is $emailAddress")
        waitingPermissionViewModel.checkCompatibility(emailAddress)

        waitingPermissionViewModel.isPermissionGranted.observe(this, Observer {
            Timber.d("isDriveCompatible $it")
            requestToLaunchMainActivity()
        })
    }


    private fun requestToLaunchMainActivity() {
        val bundle = bundleOf(EntryActivity.RESULT_MAIN_LAUNCH to true)
        requireActivity().supportFragmentManager.setFragmentResult(EntryActivity.REQ_KEY_FRAGMENT_MAIN_LAUNCH, bundle)
    }
}