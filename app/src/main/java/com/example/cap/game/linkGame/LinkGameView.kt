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

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        startPoints.clear()
        endPoints.clear()
        // 繪製左側字母
        for (i in 0 until 3) {
            val x = width * 0.2f
            val y = height * (0.2f + i * 0.3f)

            // 繪製字母背景
            val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#FFCCCCCC")
                style = Paint.Style.FILL
            }
            canvas.drawCircle(x, y, 80f, backgroundPaint)

            // 計算字母的繪製位置
            val textBounds = Rect()
            paintText.getTextBounds(leftLetters[i].toString(), 0, 1, textBounds)
            val textX = x
            val textY = y - textBounds.exactCenterY()

            // 繪製字母
            canvas.drawText(leftLetters[i].toString(), textX, textY, paintText)
            startPoints.add(PointF(x, y))
        }

        // 繪製右側字母
        for (i in 0 until 3) {
            val x = width * 0.8f
            val y = height * (0.2f + i * 0.3f)

            val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.parseColor("#FFCCCCCC")
                style = Paint.Style.FILL
            }
            canvas.drawCircle(x, y, 80f, backgroundPaint)
            val textBounds = Rect()
            paintText.getTextBounds(rightLetters[i].toString(), 0, 1, textBounds)
            val textX = x
            val textY = y - textBounds.exactCenterY()

            canvas.drawText(rightLetters[i].toString(), textX, textY, paintText)
            endPoints.add(PointF(x, y))
        }

        // 繪製連線
        for ((startIndex, endIndex) in connections) {
            val startX = startPoints[startIndex].x
            val startY = startPoints[startIndex].y
            val endX = endPoints[endIndex].x
            val endY = endPoints[endIndex].y

            // 計算連線的起點和終點座標
            val startLineX = startX + 80f
            val startLineY = startY
            val endLineX = endX - 80f
            val endLineY = endY

            // 繪製連線
            canvas.drawLine(startLineX, startLineY, endLineX, endLineY, paintLine)
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
                if (x < width / 2) {
                    for (i in startPoints.indices) {
                        if (isPointInCircle(x, y, startPoints[i], radius)) {
                            selectedStartIndex = i
                            break
                        }
                    }
                } else {
                    for (i in endPoints.indices) {
                        if (isPointInCircle(x, y, endPoints[i], radius)) {
                            selectedEndIndex = i
                            break
                        }
                    }
                }

                // 如果選擇了起點和終點,則建立連線
                if (selectedStartIndex != -1 && selectedEndIndex != -1 &&
                    selectedStartIndex < leftLetters.size && selectedEndIndex < rightLetters.size) {
                    if (leftLetters[selectedStartIndex].lowercaseChar() == rightLetters[selectedEndIndex]) {
                        performClick()
                    }
                }
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
        val randomLetters = letters.shuffled().take(3)
        leftLetters.addAll(randomLetters)
        rightLetters.addAll(randomLetters.map { it.lowercaseChar() }.shuffled())

        invalidate()
    }
}

