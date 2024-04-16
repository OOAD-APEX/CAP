package com.example.cap.ui.game.linkGame

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.cap.R
import com.example.cap.ui.game.Game

class LinkGameDialog(context: Context) : Dialog(context), Game {
    private lateinit var linkGameView: LinkGameView
    override fun onCreate(savedInstanceState: Bundle?) {
        super<Dialog>.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_link_game)

        linkGameView = findViewById(R.id.linkGameView)
        linkGameView.startGame()

        // 設置 Dialog 的屬性
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        setCancelable(false)

        // 在這裡初始化遊戲的 UI 元素和邏輯
        linkGameView.setOnGameCompletedListener(object : LinkGameView.OnGameCompletedListener {
            override fun onGameCompleted() {
                // 在遊戲完成時關閉 Dialog
                dismiss()
            }
        })
    }

    override fun startGame() {
        linkGameView.startGame()
    }

    override fun endGame() {
        // 實現結束遊戲的邏輯
    }
}
