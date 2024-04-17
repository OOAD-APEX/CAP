package com.example.cap.game.linkGame

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import com.example.cap.R
import com.example.cap.game.Game

class LinkGameDialog(context: Context) : Dialog(context), Game {
    private lateinit var linkGameView: LinkGameView
    override fun onCreate(savedInstanceState: Bundle?) {
        super<Dialog>.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_link_game)

        startGame()

        // 設置 Dialog 的屬性
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window?.insetsController?.hide(WindowInsets.Type.systemBars())
        } else {
            window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        setCancelable(false)

        // 在這裡初始化遊戲的 UI 元素和邏輯

    }

    override fun startGame() {
        linkGameView = findViewById(R.id.linkGameView)
        linkGameView.setGame(this)
        linkGameView.startGame()
    }

    override fun endGame() {
        dismiss()
    }
}
