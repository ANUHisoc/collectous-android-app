package org.anuhisoc.collectous.entry.permission


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.anuhisoc.collectous.databinding.FragmentDriveCompatibilityBinding
import org.anuhisoc.collectous.entry.EntryActivity
import org.anuhisoc.collectous.entry.EntryViewModel
import timber.log.Timber


/*TODO*/
class DriveCompatibilityFragment : Fragment() {


    private lateinit var binding: FragmentDriveCompatibilityBinding
    private val entryViewModel:EntryViewModel by activityViewModels()
    private val driveCompatibilityViewModel: DriveCompatibilityViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentDriveCompatibilityBinding.inflate(inflater,container,false)
        return binding.root
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")
        driveCompatibilityViewModel.isDriveCompatible.observe(this, Observer {
            Timber.d("isDriveComplatible $it")
            requestToLaunchMainActivity()
        })


    }

    private fun requestToLaunchMainActivity() {
        val bundle = bundleOf(EntryActivity.RESULT_MAIN_LAUNCH to true)
        requireActivity().supportFragmentManager.setFragmentResult(EntryActivity.REQ_KEY_FRAGMENT_MAIN_LAUNCH,bundle)
    }
}