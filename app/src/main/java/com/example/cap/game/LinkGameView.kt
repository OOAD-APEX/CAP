package com.example.cap.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View

class LinkGameView(context: Context) : View(context) {
    val viewModel = LinkGameViewModel()

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 80f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
    }

    private val paintLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 5f
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#FFCCCCCC")
        style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if(!viewModel.isGameStarted) {
            viewModel.startGame(w, h)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        viewModel.drawGame(canvas, paintText, paintLine, backgroundPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewModel.handleTouchEvent(event) {
            invalidate()
        }
        return true
    }
}
