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
import org.anuhisoc.collectous.databinding.FragmentDrivePermissionBinding;

public class DrivePermissionFragment extends Fragment {

    private FragmentDrivePermissionBinding binding;
    public DrivePermissionFragment(){}

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.givePermissionButton.setOnClickListener(button -> {
            Navigation.findNavController(binding.getRoot()).navigate(R.id.action_drivePermissionFragment_to_driveCompatibilityFragment);
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDrivePermissionBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
}
