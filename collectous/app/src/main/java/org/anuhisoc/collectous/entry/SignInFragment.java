package org.anuhisoc.collectous.entry;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.IntentSenderRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.StartIntentSenderForResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions;
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.identity.SignInClient;
import com.google.android.gms.auth.api.identity.SignInCredential;
import com.google.android.gms.common.api.ApiException;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.anuhisoc.collectous.databinding.FragmentSignInBinding;

import timber.log.Timber;



public class SignInFragment extends Fragment {


    private FragmentSignInBinding binding;
    private EntryViewModel entryViewModel;

    public SignInFragment() { }

    private final ActivityResultLauncher<IntentSenderRequest> signInLauncherHandler =
            registerForActivityResult(new ActivityResultContracts.StartIntentSenderForResult(), result -> {
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
        entryViewModel = new ViewModelProvider(getActivity()).get(EntryViewModel.class);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.signInButton.setOnClickListener(signInButton -> signIn());
    }


    private void signIn() {

        /*https://developers.google.com/identity/sign-in/android/sign-in-identity
        Set Client -ID; Get this from Google Cloud console*/
        GetSignInIntentRequest request =
                GetSignInIntentRequest.builder()
                        .build();

        /*https://developers.google.com/android/reference/com/google/android/gms/auth/api/identity/SignInClient*/
       /* BeginSignInRequest request = BeginSignInRequest.builder()
                .setPasswordRequestOptions(
                        BeginSignInRequest.PasswordRequestOptions.builder()
                                .setSupported(false)
                                .build())
                .setGoogleIdTokenRequestOptions(
                        GoogleIdTokenRequestOptions.builder()
                                .setSupported(false)
                                .setFilterByAuthorizedAccounts(false)
                                .build())
                .build();*/


        if (getActivity() != null) {
            Identity.getSignInClient(getActivity())
                    .getSignInIntent(request)
                    .addOnSuccessListener(
                            result -> {signInLauncherHandler.launch(new IntentSenderRequest
                                    .Builder(result.getIntentSender()).build());})
                    .addOnFailureListener(
                            e -> {
                                Timber.e("Sign In Activity failed");
                            });

            /*SignInClient signInClient = Identity.getSignInClient(getActivity());
            signInClient.beginSignIn(request)
                    .addOnSuccessListener(
                            result ->
                            {
                                try {
                                    result.getPendingIntent().send();
                                } catch (PendingIntent.CanceledException e) {
                                    e.printStackTrace();
                                }

                            })
                    .addOnFailureListener( e-> Timber.e("Sign In Activity failed"));*/

        }
    }


    private void handleSignInResult(ActivityResult result) {
        try {
            SignInCredential credential = Identity.getSignInClient(getActivity()).getSignInCredentialFromIntent(result.getData());
            Timber.d("Successful sign in ");

            Snackbar snackbar = Snackbar.make(binding.getRoot(),"Successfully Signed In", BaseTransientBottomBar.LENGTH_SHORT);

            snackbar.addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    entryViewModel.updateGoogleCredential(credential);
                    snackbar.removeCallback(this);
                }
            });
            snackbar.show();
        } catch (ApiException e) {
            Timber.d("Failed sign in %s", e.toString());
            Snackbar.make(binding.getRoot(),"Error",Snackbar.LENGTH_SHORT).show();
        }
    }


}