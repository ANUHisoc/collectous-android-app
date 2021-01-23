package org.anuhisoc.collectous.entry;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class EntryViewModel extends ViewModel {
    private MutableLiveData<GoogleSignInAccount> accountLiveData;

     LiveData<GoogleSignInAccount> getAccountLiveData() {
        if(accountLiveData == null){
            accountLiveData = new MutableLiveData<>();
        }
        return accountLiveData;
    }

    void updateGoogleAccount(GoogleSignInAccount account) {
        if (accountLiveData == null) {
            accountLiveData = new MutableLiveData<>(account);
        } else {
            accountLiveData.setValue(account);
        }
    }



}
