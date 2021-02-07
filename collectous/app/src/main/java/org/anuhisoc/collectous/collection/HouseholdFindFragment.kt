package org.anuhisoc.collectous.collection

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import org.anuhisoc.collectous.R
import org.anuhisoc.collectous.databinding.FragmentHouseholdFindBinding


class HouseholdFindFragment : Fragment() {


    private lateinit  var  binding:FragmentHouseholdFindBinding


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.floatingActionButton.setOnClickListener { findNavController().navigate(R.id.action_householdFindFragment_to_householdAddFragment) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        binding = FragmentHouseholdFindBinding.inflate(inflater,container,false)

        return binding.root
    }


}