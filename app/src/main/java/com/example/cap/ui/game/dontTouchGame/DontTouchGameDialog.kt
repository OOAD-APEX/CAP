package com.example.cap.ui.game.dontTouchGame

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import com.example.cap.R
import com.example.cap.ui.game.Game

class DontTouchGameDialog(context: Context) : Dialog(context), Game {
    override fun onCreate(savedInstanceState: Bundle?) {
        super<Dialog>.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_dont_touch_game)

        // 設置 Dialog 的屬性
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setCancelable(false)

    }

    override fun startGame() {
    }

    override fun endGame() {

    }
}
