package com.hunglee.mymusicproject.acitivity.ui.contact_us

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ContactUsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Contact Us Fragment"
    }
    val text: LiveData<String> = _text
}