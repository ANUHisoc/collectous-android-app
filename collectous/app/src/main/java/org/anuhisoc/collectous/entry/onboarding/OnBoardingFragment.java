package org.anuhisoc.collectous.entry.onboarding;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.anuhisoc.collectous.databinding.FragmentOnBoardingBinding;


public class OnBoardingFragment extends Fragment {


    private FragmentOnBoardingBinding binding;
    public OnBoardingFragment() { }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOnBoardingBinding.inflate(inflater,container,false);

        return binding.getRoot();
    }
}