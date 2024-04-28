package com.example.cap.game

import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import com.example.cap.domain.MiniGame

class LinkGameViewModel {
    private val model = MiniGame()
    var isGameStarted = false

    fun startGame(width: Int, height: Int) {
        model.generateLetters()
        model.generatePoints(width, height)
        isGameStarted = true
    }

    fun drawGame(canvas: Canvas, paintText: Paint, paintLine: Paint, backgroundPaint: Paint) {
        model.drawConnections(canvas, paintLine)
        model.drawTraceLine(canvas, paintLine)
        model.drawLetterBackground(canvas, backgroundPaint)
        model.drawLetter(canvas, paintText)
    }

    fun handleTouchEvent(event: MotionEvent, invalidateView: () -> Unit) {
        model.handleTouchEvent(event) {
            invalidateView()
        }
    }


    fun setGame(game: Game) {
        model.game = game
    }


}
