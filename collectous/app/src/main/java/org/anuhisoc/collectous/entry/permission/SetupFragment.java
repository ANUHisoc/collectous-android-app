package org.anuhisoc.collectous.entry.permission;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import org.anuhisoc.collectous.R;
import org.anuhisoc.collectous.databinding.FragmentSetupBinding;
import org.jetbrains.annotations.NotNull;

import timber.log.Timber;


public class SetupFragment extends Fragment {


    public SetupFragment() { }


    private FragmentSetupBinding binding;



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.continueButton.setOnClickListener(button -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_setupFragment_to_drivePermissionFragment);
        });

    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");
        binding = FragmentSetupBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
}