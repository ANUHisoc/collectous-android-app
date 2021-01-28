package org.anuhisoc.collectous.entry.permission

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.anuhisoc.collectous.R
import org.anuhisoc.collectous.databinding.FragmentDriveCompatibilityBinding

/*TODO*/
class DriveCompatibilityFragment : Fragment() {


    private lateinit var binding: FragmentDriveCompatibilityBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentDriveCompatibilityBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {

            /*Temporary: Just so to show there exist a drive compatibility screen;*/
            delay(4000)


            findNavController().navigate(R.id.action_driveCompatibilityFragment_to_mainActivity)
        }
    }


}