package org.anuhisoc.collectous.entry.permission

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class DriveCompatibilityViewModel : ViewModel() {

    private val _isDriveCompatible = MutableLiveData<Boolean>()
    val isDriveCompatible: LiveData<Boolean> = _isDriveCompatible


    init {

        viewModelScope.launch {
            /*Temporary: Just so to show there exist a drive compatibility screen;*/
            Timber.d("init")
            delay(4000)
            _isDriveCompatible.value = true
            Timber.d("val changed")
        }
    }



}