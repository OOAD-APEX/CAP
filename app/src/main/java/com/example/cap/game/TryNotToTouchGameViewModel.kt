package com.example.cap.game

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import com.example.cap.domain.TryNotToTouchGame

class TryNotToTouchGameViewModel {
    private val model = TryNotToTouchGame()
    private var wallsGenerated = false

    fun startGame(width: Int, height: Int) {
        model.generateWalls(width, height)
        model.setScreenSize(width, height)
        wallsGenerated = true
    }

    fun drawGame(canvas: Canvas, wirePaint: Paint, touchPaint: Paint, collisionPaint: Paint, blackPaint: Paint) {
        if (!wallsGenerated) return
        model.drawWalls(canvas, blackPaint, wirePaint)
        model.drawTouchPoint(canvas, touchPaint)
        model.drawCollisionMessage(canvas, collisionPaint)
    }

    fun handleTouchEvent(event: MotionEvent, invalidateView: () -> Unit) {
        try{
            model.handleTouchEvent(event, invalidateView)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setGame(game: Game) {
        model.setGame(game)
    }

}
