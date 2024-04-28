package com.example.cap.ui.fortune

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.TextView
import com.example.cap.R
import com.example.cap.game.LinkGameViewModel


class DailyFortuneDialog(context: Context) : Dialog(context){
    private val viewModel = DailyFortuneViewModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.daily_fortune_dialog)
        val fortuneColorTextView = findViewById<TextView>(R.id.fortune_color)
        val dailyFortuneTextView = findViewById<TextView>(R.id.daily_fortune)
        fortuneColorTextView.append(viewModel.getRandomFortuneColor())
        dailyFortuneTextView.append(viewModel.getRandomDailyFortune())
        settingDialog()
    }


    private fun settingDialog() {
        //填滿螢幕
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        //隱藏狀態列
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window?.insetsController?.hide(WindowInsets.Type.systemBars())
        } else {
            window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
    }

}
