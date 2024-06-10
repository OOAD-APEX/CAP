package com.example.cap.game

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class TryNotToTouchGameView(context: Context) : View(context) {
    val viewModel = TryNotToTouchGameViewModel()

    private val wirePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
        style = Paint.Style.STROKE
    }

    private val touchPaint = Paint().apply {
        color = Color.RED
        strokeWidth = 80f
    }

    private val collisionPaint = Paint().apply {
        color = Color.RED
        textSize = 100f
    }

    private val blackPaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        viewModel.drawGame(canvas, wirePaint, touchPaint, collisionPaint, blackPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        viewModel.handleTouchEvent(event, ::invalidate)
        return true
    }
}
