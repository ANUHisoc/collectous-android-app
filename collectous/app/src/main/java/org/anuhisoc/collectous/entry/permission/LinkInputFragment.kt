package org.anuhisoc.collectous.entry.permission

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import androidx.core.os.bundleOf
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import org.anuhisoc.collectous.R
import org.anuhisoc.collectous.databinding.FragmentLinkInputBinding
import timber.log.Timber


class LinkInputFragment : Fragment() {

    private lateinit var binding: FragmentLinkInputBinding
    private var link = ""

    companion object{
        private const val LINK_STRING_KEY = "link_text"
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.continueButton.setOnClickListener {
            findNavController().navigate(R.id.action_linkInputFragment_to_sheetCompatibilityFragment, bundleOf("link" to link))}


        binding.linkTextInputLayout.apply{
            editText?.doOnTextChanged { charSequence, _, _, _ ->
                val inputText = charSequence?.toString()
                val isValidUrl = URLUtil.isValidUrl(inputText)
                val isTextEmpty = inputText?.isEmpty()?:false
                inputText?.let { link = it }
                Timber.d("Input text is $inputText is Valid URL $isValidUrl  ")
                if(isValidUrl){
                    showContinueButton()
                    error = null }
                if(isTextEmpty)
                    error = null
                else if(!isValidUrl)
                    error = "URL not valid."
                if(error!=null)
                    hideContinueButton()
            }
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let { bundle ->
            binding.linkTextInputLayout.editText?.text?.insert(0,bundle.getString(LINK_STRING_KEY)) }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LINK_STRING_KEY,link)
    }

    private fun hideContinueButton() {
        binding.continueButton.visibility = View.INVISIBLE
    }



    private fun showContinueButton() {
        binding.continueButton.visibility=View.VISIBLE
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = FragmentLinkInputBinding.inflate(inflater,container,false)
        return binding.root
    }



}