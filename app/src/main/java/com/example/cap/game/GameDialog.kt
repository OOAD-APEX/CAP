package com.example.cap.game

import android.app.Application
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.widget.TextView
import com.example.cap.R
import kotlin.random.Random

class GameDialog(context: Context) : Dialog(context), Game {
    private lateinit var selectedGameView: View
    private val observers = mutableListOf<GameDialogObserver>()
    private val gameViewList: List<View> = listOf( //增加遊戲
        LinkGameView(context),
        TryNotToTouchGameView(context)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super<Dialog>.onCreate(savedInstanceState)
        setContentView(R.layout.game_dialog)
        settingDialog()
        startGame()
    }

    override fun startGame() {
        selectedGameView = selectRandomGameView()

        // 將選擇的遊戲 View 加入到 Dialog 的內容視圖中
        val container = findViewById<ViewGroup>(R.id.gameContainer)
        container.addView(selectedGameView)
        val gameHintTextView = findViewById<TextView>(R.id.gameHintTextView)

        // 設置遊戲並啟動
        when (selectedGameView) {
            is LinkGameView -> {
                val viewModel = (selectedGameView as LinkGameView).viewModel
                viewModel.setGame(this)
                selectedGameView.post {
                    val width = selectedGameView.width
                    val height = selectedGameView.height
                    viewModel.startGame(width, height)
                    gameHintTextView.setText(R.string.link_game_hint)
                }
            }
            is TryNotToTouchGameView -> {
                val viewModel = (selectedGameView as TryNotToTouchGameView).viewModel
                viewModel.setGame(this)
                selectedGameView.post {
                    val width = selectedGameView.width
                    val height = selectedGameView.height
                    viewModel.startGame(width, height)
                    gameHintTextView.setText(R.string.try_not_to_touch_game_hint)
                }
            }
        }
    }

    override fun endGame() {
        dismiss()
        notifyGameComplete()
    }

    fun addObserver(observer: GameDialogObserver) {
        observers.add(observer)
    }

    private fun notifyGameComplete() {
        observers.forEach { it.onGameComplete() }
    }

    private fun selectRandomGameView(): View {
        val randomIndex = Random.nextInt(gameViewList.size)
        return gameViewList[randomIndex]
    }

    private fun settingDialog() {
        //填滿螢幕
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        //背景透明?
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        //隱藏狀態列
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window?.insetsController?.hide(WindowInsets.Type.systemBars())
        } else {
            window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        }
        //不可上一頁
        setCancelable(false)
    }
}
