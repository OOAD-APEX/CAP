package com.example.cap.ui.game.linkGame

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.cap.ui.game.GameState

class LinkGameView(context: Context, attrs: AttributeSet) : View(context, attrs) {
    private val letters = ('A'..'Z').toList()
    private val startPoints = mutableListOf<PointF>()
    private val endPoints = mutableListOf<PointF>()
    private val connections = mutableListOf<Pair<Int, Int>>()
    private var gameState = GameState.READY
    private var leftLetters = mutableListOf<Char>()
    private var rightLetters = mutableListOf<Char>()
    private var selectedStartIndex = -1
    private var selectedEndIndex = -1

    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        textSize = 80f
        textAlign = Paint.Align.CENTER
    }

    private val paintLine = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = 5f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // 繪製左側字母
        for (i in 0 until 3) {
            val x = width * 0.2f
            val y = height * (0.2f + i * 0.3f)
            canvas.drawText(leftLetters[i].toString(), x, y, paintText)
            startPoints.add(PointF(x, y))
        }

        // 繪製右側字母
        for (i in 0 until 3) {
            val x = width * 0.8f
            val y = height * (0.2f + i * 0.3f)
            canvas.drawText(rightLetters[i].toString(), x, y, paintText)
            endPoints.add(PointF(x, y))
        }

        // 繪製連線
        for ((startIndex, endIndex) in connections) {
            canvas.drawLine(startPoints[startIndex].x, startPoints[startIndex].y,
                endPoints[endIndex].x, endPoints[endIndex].y, paintLine)
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
                        connections.add(Pair(selectedStartIndex, selectedEndIndex))
                        selectedStartIndex = -1
                        selectedEndIndex = -1
                        invalidate()

                        // 檢查是否完成所有連線
                        if (connections.size == 3) {
                            gameState = GameState.COMPLETED
                            // 觸發遊戲完成的回調或處理
                            onGameCompletedListener?.onGameCompleted()
                        }
                    }
                }
            }
        }
        return true
    }

    fun startGame() {
        gameState = GameState.READY
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

    interface OnGameCompletedListener {
        fun onGameCompleted()
    }

    // 在 LinkGameView 中新增一個變數來儲存監聽器
    private var onGameCompletedListener: OnGameCompletedListener? = null

    // 提供一個方法來設置監聽器
    fun setOnGameCompletedListener(listener: OnGameCompletedListener) {
        onGameCompletedListener = listener
    }


}

