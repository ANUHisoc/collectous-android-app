package org.anuhisoc.collectous.entry.permission


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.viewinterop.viewModel
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.Scope
import org.anuhisoc.collectous.databinding.FragmentDriveCompatibilityBinding
import org.anuhisoc.collectous.entry.EntryActivity
import org.anuhisoc.collectous.entry.EntryViewModel
import timber.log.Timber



class DriveCompatibilityFragment : Fragment() {


    private lateinit var binding: FragmentDriveCompatibilityBinding
    private val driveCompatibilityViewModel: DriveCompatibilityViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentDriveCompatibilityBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        /*driveCompatibilityViewModel.checkCompatibility()*/
        driveCompatibilityViewModel.isDriveCompatible.observe(this, Observer {
            Timber.d("isDriveCompatible $it")
            requestToLaunchMainActivity()
        })
    }


    private fun requestToLaunchMainActivity() {
        val bundle = bundleOf(EntryActivity.RESULT_MAIN_LAUNCH to true)
        requireActivity().supportFragmentManager.setFragmentResult(EntryActivity.REQ_KEY_FRAGMENT_MAIN_LAUNCH, bundle)
    }
}