package org.anuhisoc.collectous.entry.setup;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.anuhisoc.collectous.R;
import org.anuhisoc.collectous.databinding.FragmentDrivePermissionBinding;

import timber.log.Timber;

public class DrivePermissionFragment extends Fragment {

    private FragmentDrivePermissionBinding binding;
    private PermissionViewModel permissionViewModel;


    private final ActivityResultLauncher<Intent> drivePermissionLauncherHandler
            = registerForActivityResult(new StartActivityForResult(), this::handleDrivePermissionResult);


    private void startDriveCompatibility(){
        Navigation.findNavController(requireActivity(), R.id.entry_nav_host_fragment)
                .navigate(R.id.action_drivePermissionFragment_to_emailInputFragment);
    }


    private void handleDrivePermissionResult(ActivityResult result) {
        Timber.d("%s", result.getResultCode());
        Task<GoogleSignInAccount>  task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
        try {
            task.getResult(ApiException.class);
            startDriveCompatibility();
        } catch (ApiException e) {
            Timber.d("Drive permission not provided");
        }
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        permissionViewModel = new ViewModelProvider(requireActivity()).get(PermissionViewModel.class);
        binding.givePermissionButton.setOnClickListener(button -> checkForDrivePermissions());
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDrivePermissionBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    private void checkForDrivePermissions() {
        if (!permissionViewModel.getHasDrivePermission()) {
            Timber.d("no permission");
            Intent signInIntent = permissionViewModel.getGoogleSignInClient().getSignInIntent();
            drivePermissionLauncherHandler.launch(signInIntent);
        } else {
            Timber.d("has permission");
            startDriveCompatibility();
        }
    }

}
