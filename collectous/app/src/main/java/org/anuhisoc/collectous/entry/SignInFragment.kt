package org.anuhisoc.collectous.entry

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Status
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.BaseTransientBottomBar.BaseCallback
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.anuhisoc.collectous.R
import org.anuhisoc.collectous.databinding.FragmentSignInBinding
import timber.log.Timber

class SignInFragment : Fragment() {
    private lateinit var binding: FragmentSignInBinding

    private val signInViewModel: SignInViewModel by viewModels()
    private val signInLauncherHandler = registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
        Timber.d("Sign In Activity Result %s", result.toString())
        handleSignInResult(result)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentSignInBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signInButton.setOnClickListener { launchSignInProcess() }

        signInViewModel.isSuccessSnackBarDismissed.observe(viewLifecycleOwner, Observer {
            isSuccessSnackBarDismissed -> if(isSuccessSnackBarDismissed==true){
            findNavController().navigate(R.id.action_signInFragment_to_setup, bundleOf("name" to signInViewModel.accountName))
        } })

    }

    private fun launchSignInProcess() {
        Timber.d("signIn process launched")
        val signInIntent = signInViewModel.googleSignInClient.signInIntent
        signInLauncherHandler.launch(signInIntent)
    }


    private fun handleSignInResult(result: ActivityResult) {
        try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            Timber.d("Successful sign in ")
            val account = task.getResult(ApiException::class.java)
            lifecycleScope.launch {
                val isAccUpdatedSuccessfully = signInViewModel.updateGoogleAccount(account)
                if(isAccUpdatedSuccessfully){
                    launchSignInSuccessFeedback()
                }else{
                    throw ApiException(Status(Status.RESULT_CANCELED.statusCode))
                }
            }
        } catch (e: ApiException) {
            Timber.d("Failed sign in %s", e.toString())
            Snackbar.make(binding.root, "Error", Snackbar.LENGTH_SHORT).show()
        }
    }


    private fun launchSignInSuccessFeedback(){
        val snackBar = Snackbar.make(binding.root, "Successfully signed in!", BaseTransientBottomBar.LENGTH_SHORT)
        snackBar.addCallback(object : BaseCallback<Snackbar?>() {
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                super.onDismissed(transientBottomBar, event)
                signInViewModel.setSnackBarDismissed()
                snackBar.removeCallback(this)
            }
        })
        snackBar.show()
    }
}