package org.anuhisoc.collectous.entry;

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

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.anuhisoc.collectous.databinding.FragmentSignInBinding;

import timber.log.Timber;



public class SignInFragment extends Fragment {


    private FragmentSignInBinding binding;
    private EntryViewModel entryViewModel;
    private GoogleSignInClient googleSignInClient;

    public SignInFragment() { }

    private final ActivityResultLauncher<Intent> signInLauncherHandler =
            registerForActivityResult(new StartActivityForResult(), result -> {
                Timber.d("Sign In Activity Result %s", result.toString());
                handleSignInResult(result);
            });


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity()!=null)
            entryViewModel = new ViewModelProvider(getActivity()).get(EntryViewModel.class);
        GoogleSignInOptions signInOption = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        if(getContext()!=null)
            googleSignInClient = GoogleSignIn.getClient(getContext(),signInOption);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.signInButton.setOnClickListener(signInButton -> signIn());
    }


    private void signIn(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        signInLauncherHandler.launch(signInIntent);
    }


    private void handleSignInResult(ActivityResult result) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
            Timber.d("Successful sign in ");
            GoogleSignInAccount account = task.getResult(ApiException.class);

            Snackbar snackbar = Snackbar.make(binding.getRoot(),"Successfully Signed In", BaseTransientBottomBar.LENGTH_SHORT);

            snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    entryViewModel.updateGoogleAccount(account);
                    snackbar.removeCallback(this);
                }
            });
            snackbar.show();

        } catch (ApiException e) {
            Timber.d("Failed sign in ");
            Snackbar.make(binding.getRoot(),"Error",Snackbar.LENGTH_SHORT).show();
        }
    }

}
