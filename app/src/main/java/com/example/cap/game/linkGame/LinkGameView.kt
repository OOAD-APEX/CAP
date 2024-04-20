package com.example.cap.game.linkGame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.cap.game.Game

class LinkGameView(context: Context, attrs: AttributeSet) : View(context, attrs) {
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
    private var endX = 0f
    private var endY = 0f
    private var isDrawingLine = false
    private val linkCount = 3 //設定連線數量
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

    private fun generatePoints(count: Int) {

        startPoints.clear()
        endPoints.clear()
        for (i in 0 until count) {
            val leftX = width * 0.2f
            val rightX = width * 0.8f
            val y = height * (0.2f + i * (0.6f / (count - 1)))
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
            canvas.drawCircle(startPoints[i].x, startPoints[i].y, 80f, backgroundPaint)
        }
        for(i in endPoints.indices){
            canvas.drawCircle(endPoints[i].x, endPoints[i].y, 80f, backgroundPaint)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        generatePoints(linkCount)
        // 繪製連線
        for ((startIndex, endIndex) in connections) {
            // 繪製連線
            canvas.drawLine(startPoints[startIndex].x, startPoints[startIndex].y,
                endPoints[endIndex].x, endPoints[endIndex].y, paintLine)
        }
        drawLetterBackground(canvas, startPoints, endPoints)
        drawLetter(canvas, startPoints, endPoints)
        if (isDrawingLine) {
            canvas.drawLine(startX, startY, endX, endY, paintLine)
        }
    }

    private fun isPointInCircle(x: Float, y: Float, center: PointF, radius: Float): Boolean {
        val dx = x - center.x
        val dy = y - center.y
        return dx * dx + dy * dy <= radius * radius
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                val radius = 80f

                // 檢查點擊的是左側還是右側字母
                for (i in startPoints.indices) {
                    if (isPointInCircle(x, y, startPoints[i], radius)) {
                        selectedStartIndex = i
                        startX = startPoints[i].x
                        startY = startPoints[i].y
                        isDrawingLine = true
                        break
                    }
                }
                if (selectedStartIndex == -1) {
                    for (i in endPoints.indices) {
                        if (isPointInCircle(x, y, endPoints[i], radius)) {
                            selectedEndIndex = i
                            startX = endPoints[i].x
                            startY = endPoints[i].y
                            isDrawingLine = true
                            break
                        }
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                endX = event.x
                endY = event.y
                invalidate()
            }
            MotionEvent.ACTION_UP -> {
                val x = event.x
                val y = event.y
                val radius = 80f

                if (selectedStartIndex != -1) {
                    for (i in endPoints.indices) {
                        if (isPointInCircle(x, y, endPoints[i], radius)) {
                            selectedEndIndex = i
                            break
                        }
                    }
                } else if (selectedEndIndex != -1) {
                    for (i in startPoints.indices) {
                        if (isPointInCircle(x, y, startPoints[i], radius)) {
                            selectedStartIndex = i
                            break
                        }
                    }
                }

                if (selectedStartIndex != -1 && selectedEndIndex != -1 &&
                    selectedStartIndex < leftLetters.size && selectedEndIndex < rightLetters.size) {
                    if (leftLetters[selectedStartIndex].lowercaseChar() == rightLetters[selectedEndIndex]) {
                        performClick()
                    }
                }
                selectedStartIndex = -1
                selectedEndIndex = -1
                isDrawingLine = false
                invalidate()
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()

        if (selectedStartIndex != -1 && selectedEndIndex != -1 &&
            selectedStartIndex < leftLetters.size && selectedEndIndex < rightLetters.size) {
            connections.add(Pair(selectedStartIndex, selectedEndIndex))
            selectedStartIndex = -1
            selectedEndIndex = -1
            invalidate()

            // 檢查是否完成所有連線
            if (connections.size == 3) {
                game.endGame()
            }
        }

        return true
    }

    fun startGame() {
        leftLetters.clear()
        rightLetters.clear()
        connections.clear()
        startPoints.clear()
        endPoints.clear()

        // 隨機生成左右兩側的字母
        val randomLetters = letters.shuffled().take(linkCount)
        leftLetters.addAll(randomLetters)
        rightLetters.addAll(randomLetters.map { it.lowercaseChar() }.shuffled())

        invalidate()
    }
}

