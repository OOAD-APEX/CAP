package com.example.cap.ui.alarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlarmViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "8 : 00 AM"
    }

    val text: LiveData<String> = _text

}