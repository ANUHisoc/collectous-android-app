package org.anuhisoc.collectous.entry;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    private GoogleSignInClient googleSignInClient;
    private FragmentSignInBinding binding;
    private final int REQ_CODE_SIGN_IN = 1;
    private EntryViewModel entryViewModel;

    public SignInFragment() { }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSignInBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        GoogleSignInOptions signInOption = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getContext(),signInOption);

        entryViewModel = new ViewModelProvider(getActivity()).get(EntryViewModel.class);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.signInButton.setOnClickListener(signInButton -> {
            signIn();
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Timber.d("onActivityResult called "+ requestCode +" " + resultCode  +" " + data );
        if(requestCode == REQ_CODE_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }


    private void signIn(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQ_CODE_SIGN_IN);
    }


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            Timber.d("Successful sign in ");
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

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