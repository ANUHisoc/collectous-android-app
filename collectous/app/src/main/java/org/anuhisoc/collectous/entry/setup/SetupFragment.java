package org.anuhisoc.collectous.entry.setup;

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


    private FragmentSetupBinding binding;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        String name = "";
        if(getArguments()!=null) {
            name = getArguments().getString("name", "");
        }

        binding.welcomeTextView.setText(getWelcomeMessage(name));

        binding.continueButton.setOnClickListener(button -> Navigation.findNavController(view).navigate(R.id.action_setupFragment_to_drivePermissionFragment));

    }


    private String getWelcomeMessage(String name){
        String defaultWelcomeMessage = getResources().getString(R.string.setup_welcome_message);
        return !name.isEmpty()? defaultWelcomeMessage +" " +name +"!": defaultWelcomeMessage +"!";
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");
        binding = FragmentSetupBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
}