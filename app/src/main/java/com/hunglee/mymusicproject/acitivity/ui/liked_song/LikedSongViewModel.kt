package com.hunglee.mymusicproject.acitivity.ui.liked_song

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LikedSongViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Liked Song Fragment"
    }
    val text: LiveData<String> = _text
}