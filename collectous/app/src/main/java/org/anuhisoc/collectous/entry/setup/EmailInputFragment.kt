package org.anuhisoc.collectous.entry.setup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import org.anuhisoc.collectous.R
import org.anuhisoc.collectous.databinding.FragmentEmailInputBinding
import org.anuhisoc.collectous.entry.EntryViewModel
import org.anuhisoc.collectous.isValidEmail
import timber.log.Timber


class EmailInputFragment : Fragment() {

    private lateinit var binding: FragmentEmailInputBinding
    private var emailAddress = ""

    companion object{
        private const val EMAIL_STRING_KEY = "email_text"
    }

    private val emailInputViewModel: EmailInputViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.continueButton.setOnClickListener {
            findNavController().navigate(R.id.action_emailInputFragment_to_waitingPermissionFragment, bundleOf("email" to emailAddress))}

        emailInputViewModel.ngoAdminEmail.observe(viewLifecycleOwner, Observer {
                ngoEmail-> binding.emailTextInputLayout.editText?.setText(ngoEmail)

        })
        binding.emailTextInputLayout.apply{
            editText?.doOnTextChanged { charSequence, _, _, _ ->
                val inputText = charSequence?.toString()
                val isValidEmail = charSequence?.isValidEmail() ?: false
                val isTextEmpty = inputText?.isEmpty()?:false
                inputText?.let { emailAddress = it }
                Timber.d("Input text is $inputText is Valid Email $isValidEmail  ")
                if(isValidEmail){
                    showContinueButton()
                    error = null }
                if(isTextEmpty)
                    error = null
                else if(!isValidEmail)
                    error = "Email address not valid."
                if(error!=null)
                    hideContinueButton()
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let { bundle ->
            binding.emailTextInputLayout.editText?.text?.insert(0,bundle.getString(EMAIL_STRING_KEY)) }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EMAIL_STRING_KEY,emailAddress)
    }

    private fun hideContinueButton() {
        binding.continueButton.visibility = View.INVISIBLE
    }



    private fun showContinueButton() {
        binding.continueButton.visibility=View.VISIBLE
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentEmailInputBinding.inflate(inflater,container,false)
        return binding.root
    }



}