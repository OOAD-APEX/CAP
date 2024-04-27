package com.example.cap.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.view.MotionEvent
import android.view.View

class LinkGameView(context: Context) : View(context) {
    private val letters = ('A'..'Z').toList()
    private val startPoints = mutableListOf<PointF>()
    private val endPoints = mutableListOf<PointF>()
    private val connections = mutableListOf<Pair<Int, Int>>()
    private var leftLetters = mutableListOf<Char>()
    private var rightLetters = mutableListOf<Char>()
    private var selectedStartIndex = -1
    private var selectedEndIndex = -1
    private var startX = 0f
    private var startY = 0f
    private var currentX = 0f
    private var currentY = 0f
    private var isDrawingLine = false
    private val linkCount = 3 //設定連線數量 2~10
    private val radius = 80f  //字母背景半徑&觸碰半徑
    private lateinit var game: Game

    fun setGame(game: Game) {
        this.game = game
    }

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

    private fun generatePoints() {

        startPoints.clear()
        endPoints.clear()
        for (i in 0 until linkCount) {
            val leftX = width * 0.2f
            val rightX = width * 0.8f
            val y = height * (0.2f + i * (0.6f / (linkCount - 1)))
            startPoints.add(PointF(leftX, y))
            endPoints.add(PointF(rightX, y))
        }

    }
    private fun drawLetter(canvas: Canvas, startPoints: List<PointF>, endPoints: List<PointF>) {
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

    private fun drawLetterBackground(canvas: Canvas, startPoints: List<PointF>, endPoints: List<PointF>) {
        for(i in startPoints.indices){
            canvas.drawCircle(startPoints[i].x, startPoints[i].y, radius, backgroundPaint)
        }
        for(i in endPoints.indices){
            canvas.drawCircle(endPoints[i].x, endPoints[i].y, radius, backgroundPaint)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        generatePoints()
        // 繪製連線
        for ((startIndex, endIndex) in connections) {
            // 繪製連線
            canvas.drawLine(startPoints[startIndex].x, startPoints[startIndex].y,
                endPoints[endIndex].x, endPoints[endIndex].y, paintLine)
        }
        if (isDrawingLine) {
            canvas.drawLine(startX, startY, currentX, currentY, paintLine)
        }
        drawLetterBackground(canvas, startPoints, endPoints)
        drawLetter(canvas, startPoints, endPoints)
    }

    private fun isPointInCircle(x: Float, y: Float, center: PointF): Boolean {
        val dx = x - center.x
        val dy = y - center.y
        return dx * dx + dy * dy <= radius * radius
    }

    private fun findSelectedIndex(x: Float, y: Float, points: List<PointF>): Int {
        for (i in points.indices) {
            if (isPointInCircle(x, y, points[i])) {
                return i
            }
        }
        return -1
    }

    private fun isValidConnection(startIndex: Int, endIndex: Int): Boolean {
        return startIndex != -1 && endIndex != -1 &&
                startIndex < leftLetters.size && endIndex < rightLetters.size
    }

    private fun isMatchingLetters(startIndex: Int, endIndex: Int): Boolean {
        return leftLetters[startIndex].lowercaseChar() == rightLetters[endIndex]
    }

    private fun resetSelection() {
        selectedStartIndex = -1
        selectedEndIndex = -1
        isDrawingLine = false
    }

    private fun isAlreadyConnected(index: Int, isStartPoint: Boolean): Boolean {
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

    override fun onTouchEvent(event: MotionEvent): Boolean {
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
                invalidate()
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
                        invalidate()
                        if (connections.size == linkCount) {
                            game.endGame()
                        }
                    }
                }
                resetSelection()
                invalidate()
            }
        }
        return true
    }

    fun startGame() {
        leftLetters.clear()
        rightLetters.clear()
        connections.clear()

        // 隨機生成左右兩側的字母
        val randomLetters = letters.shuffled().take(linkCount)
        leftLetters.addAll(randomLetters)
        rightLetters.addAll(randomLetters.map { it.lowercaseChar() }.shuffled())

        invalidate()
    }
}
