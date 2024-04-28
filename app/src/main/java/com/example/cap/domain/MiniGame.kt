package com.example.cap.domain

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.view.MotionEvent
import com.example.cap.game.Game

open class MiniGame {
    private val letters = ('A'..'Z').toList()
    protected var startPoints = mutableListOf<PointF>()
    protected var endPoints = mutableListOf<PointF>()
    protected var connections = mutableListOf<Pair<Int, Int>>()
    protected var leftLetters = mutableListOf<Char>()
    protected var rightLetters = mutableListOf<Char>()
    protected var selectedStartIndex = -1
    protected var selectedEndIndex = -1
    private var startX = 0f
    private var startY = 0f
    private var currentX = 0f
    private var currentY = 0f
    protected var isDrawingLine = false
    protected val linkCount = 3 //設定連線數量 2~10
    protected val radius = 80f  //字母背景半徑&觸碰半徑
    private var game: Game? = null

    fun setGame(game: Game) {
        this.game = game
    }

    fun generateLetters(){
        leftLetters.clear()
        rightLetters.clear()
        connections.clear()
        // 隨機生成左右兩側的字母
        val randomLetters = letters.shuffled().take(linkCount)
        leftLetters.addAll(randomLetters)
        rightLetters.addAll(randomLetters.map { it.lowercaseChar() }.shuffled())
    }

    fun generatePoints(width: Int, height: Int) {
        for (i in 0 until linkCount) {
            val leftX = width * 0.2f
            val rightX = width * 0.8f
            val y = height * (0.2f + i * (0.6f / (linkCount - 1)))
            startPoints.add(PointF(leftX, y))
            endPoints.add(PointF(rightX, y))
        }
    }

    fun drawLetter(canvas: Canvas, paintText: Paint) {
        // 繪製左側字母
        for (i in startPoints.indices) {
            val textBounds = Rect()
            val letter = leftLetters[i]
            paintText.getTextBounds(letter.toString(), 0, 1, textBounds) //取得文字長度
            val x = startPoints[i].x
            val y = startPoints[i].y - textBounds.exactCenterY()   //y軸扣掉與英文字母基線的差值
            canvas.drawText(letter.toString(), x, y, paintText)
        }

        // 繪製右側字母
        for (i in endPoints.indices) {
            val textBounds = Rect()
            val letter = rightLetters[i]
            paintText.getTextBounds(letter.toString(), 0, 1, textBounds)
            val x = endPoints[i].x
            val y = endPoints[i].y - textBounds.exactCenterY()
            canvas.drawText(letter.toString(), x, y, paintText)
        }
    }

    fun drawLetterBackground(canvas: Canvas, backgroundPaint: Paint) {
        for(point in startPoints){
            canvas.drawCircle(point.x, point.y, radius, backgroundPaint)
        }
        for(point in endPoints){
            canvas.drawCircle(point.x, point.y, radius, backgroundPaint)
        }
    }

    fun drawConnections(canvas: Canvas, paintLine: Paint) {
        for ((startIndex, endIndex) in connections) {
            // 繪製連線
            canvas.drawLine(startPoints[startIndex].x, startPoints[startIndex].y,
                endPoints[endIndex].x, endPoints[endIndex].y, paintLine)
        }
    }

    fun drawTraceLine(canvas: Canvas, paintLine: Paint) {
        if (isDrawingLine) {
            canvas.drawLine(startX, startY, currentX, currentY, paintLine)
        }
    }

    protected fun isPointInCircle(x: Float, y: Float, center: PointF): Boolean {
        val dx = x - center.x
        val dy = y - center.y
        return dx * dx + dy * dy <= radius * radius
    }

    protected fun findSelectedIndex(x: Float, y: Float, points: List<PointF>): Int {
        for (i in points.indices) {
            if (isPointInCircle(x, y, points[i])) {
                return i
            }
        }
        return -1
    }

    protected fun isValidConnection(startIndex: Int, endIndex: Int): Boolean {
        return startIndex != -1 && endIndex != -1 &&
                startIndex < leftLetters.size && endIndex < rightLetters.size
    }

    protected fun isMatchingLetters(startIndex: Int, endIndex: Int): Boolean {
        return leftLetters[startIndex].lowercaseChar() == rightLetters[endIndex]
    }

    protected fun resetSelection() {
        selectedStartIndex = -1
        selectedEndIndex = -1
        isDrawingLine = false
    }

    protected fun isAlreadyConnected(index: Int, isStartPoint: Boolean): Boolean {
        if (index == -1) {
            throw IllegalArgumentException("index cant be -1")
        }
        return if (isStartPoint) {
            connections.any { it.first == index }
        } else {
            connections.any { it.second == index }
        }
    }

    private fun settingTraceStartPoint(index: Int, point: List<PointF>){
        startX = point[index].x
        startY = point[index].y
    }

   fun handleTouchEvent(event: MotionEvent, invalidateView: () -> Unit): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y

                selectedStartIndex = findSelectedIndex(x, y, startPoints)
                if (selectedStartIndex != -1 && !isAlreadyConnected(selectedStartIndex, true)) {
                    settingTraceStartPoint(selectedStartIndex, startPoints)
                    isDrawingLine = true
                } else {
                    selectedEndIndex = findSelectedIndex(x, y, endPoints)
                    if (selectedEndIndex != -1 && !isAlreadyConnected(selectedEndIndex, false)) {
                        settingTraceStartPoint(selectedEndIndex, endPoints)
                        isDrawingLine = true
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                currentX = event.x
                currentY = event.y
                invalidateView()
            }
            MotionEvent.ACTION_UP -> {
                val x = event.x
                val y = event.y
                //如果是起點開始
                if (selectedStartIndex != -1) {
                    selectedEndIndex = findSelectedIndex(x, y, endPoints)
                } else if (selectedEndIndex != -1) {
                    selectedStartIndex = findSelectedIndex(x, y, startPoints)
                }

                if (isValidConnection(selectedStartIndex, selectedEndIndex) &&
                    isMatchingLetters(selectedStartIndex, selectedEndIndex)) {
                    if (!isAlreadyConnected(selectedStartIndex, true) && !isAlreadyConnected(selectedEndIndex, false)) {
                        connections.add(Pair(selectedStartIndex, selectedEndIndex))
                        resetSelection()
                        invalidateView()
                        if (connections.size == linkCount) {
                            // if game is not null, end the game, if it is null, throw an exception
                            game?.endGame() ?: throw IllegalStateException("Game is not set")
                        }
                    }
                }
                resetSelection()
                invalidateView()
            }
        }
        return true
    }
}
