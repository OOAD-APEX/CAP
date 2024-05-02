package com.example.cap.ui.home

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.cap.game.GameDialog
import com.example.cap.game.GameDialogObserver

class HomeViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "CAP"
    }
    val text: LiveData<String> = _text

}
