package org.anuhisoc.collectous.collection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import org.anuhisoc.collectous.databinding.FragmentHouseholdAddBinding
import timber.log.Timber


class HouseholdAddFragment : Fragment() {

    private lateinit var binding:FragmentHouseholdAddBinding
    private val householdAddViewModel: HouseholdAddViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentHouseholdAddBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")
        if(savedInstanceState==null) {
            lifecycleScope.launch {
                Timber.d(householdAddViewModel.fetchHeader()?.toString())
            }
        }


    }
}