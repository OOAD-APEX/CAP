package com.example.cap.ui.fortune

import androidx.lifecycle.ViewModel
import com.example.cap.domain.DailyFortune

class DailyFortuneViewModel() : ViewModel(){
    private val model = DailyFortune()
    fun getRandomFortuneColor(): String {
        return model.getRandomFortuneColor()
    }

    fun getRandomDailyFortune(): String {
        return model.getRandomDailyFortune()
    }
}
