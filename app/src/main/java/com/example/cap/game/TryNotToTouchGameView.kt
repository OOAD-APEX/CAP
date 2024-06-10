package com.example.cap.game

import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import kotlin.math.sqrt

class TryNotToTouchGameView(context: Context) : View(context) {
    private var path1 = mutableListOf<Pair<Float, Float>>()
    private var path2 = mutableListOf<Pair<Float, Float>>()
    private var touchX = 0f
    private var touchY = 0f
    private var gameStarted = false
    private var collisionDetected = false
    private var game: Game? = null

    private val radius = 80f  //觸碰半徑&圓圈半徑
    private var startX = 0f
    private var startY = 0f


    fun setGame(game: Game) {
        this.game = game
    }

    private val wirePaint = Paint().apply {
        color = Color.BLACK
        strokeWidth = 10f
        style = Paint.Style.STROKE
    }

    private val touchPaint = Paint().apply {
        color = Color.RED
        strokeWidth = radius
    }

    private val collisionPaint = Paint().apply {
        color = Color.GREEN
        textSize = radius
    }

    private val blackPaint = Paint().apply {
        color = Color.LTGRAY
        style = Paint.Style.FILL
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        // Define the path of the wire based on screen size
        path1.clear()
        path1.add(Pair(w * 0.0f, h * 0.2f))
        path1.add(Pair(w * 0.6f, h * 0.2f))
        path1.add(Pair(w * 0.6f, h * 0.7f))
        path1.add(Pair(w * 1f, h * 0.7f))

        // Define the second path of the wire based on screen size
        path2.clear()
        path2.add(Pair(w * 0.0f, h * 0.35f))
        path2.add(Pair(w * 0.3f, h * 0.35f))
        path2.add(Pair(w * 0.3f, h * 0.85f))
        path2.add(Pair(w * 1f, h * 0.85f))

        startX = w * 0.05f
        startY = h * 0.275f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val topPath = Path().apply {
            moveTo(0f, 0f)
            lineTo(width.toFloat(), 0f)
            lineTo(path1[3].first, path1[3].second)
            lineTo(path1[2].first, path1[2].second)
            lineTo(path1[1].first, path1[1].second)
            lineTo(path1[0].first, path1[1].second)
            close()
        }
        canvas.drawPath(topPath, blackPaint)

        // Draw black area on bottom
        val bottomPath = Path().apply {
            moveTo(0f, height.toFloat())
            lineTo(width.toFloat(), height.toFloat())
            lineTo(path2[3].first, path2[3].second)
            lineTo(path2[2].first, path2[2].second)
            lineTo(path2[1].first, path2[1].second)
            lineTo(path2[0].first, path2[0].second)
            close()
        }
        canvas.drawPath(bottomPath, blackPaint)

        // Draw the first wire
        drawPath(canvas, path1)

        // Draw the second wire
        drawPath(canvas, path2)

        // Draw the touch point
        if (gameStarted) {
            canvas.drawCircle(touchX, touchY, radius, touchPaint)
        } else {
            // Draw start point
            canvas.drawCircle(startX, startY, radius, touchPaint)
        }

        // Draw collision message
        if (collisionDetected) {
            canvas.drawText("清醒點!", width * 0.5f, height * 0.1f, collisionPaint)
        }
    }

    private fun drawPath(canvas: Canvas, path: List<Pair<Float, Float>>) {
        for (i in 0 until path.size - 1) {
            val startX = path[i].first
            val startY = path[i].second
            val endX = path[i + 1].first
            val endY = path[i + 1].second
            canvas.drawLine(startX, startY, endX, endY, wirePaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!gameStarted && distance(event.x, event.y, startX, startY) <= radius) {
                    touchX = event.x
                    touchY = event.y
                    gameStarted = true
                    collisionDetected = false
                    invalidate()
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (gameStarted) {
                    val previousX = touchX
                    val previousY = touchY
                    touchX = event.x
                    touchY = event.y

                    val steps = 10
                    for (i in 1..steps) {
                        val interpolatedX = previousX + (touchX - previousX) * i / steps
                        val interpolatedY = previousY + (touchY - previousY) * i / steps

                        if (checkCollision(interpolatedX, interpolatedY, path1) || checkCollision(interpolatedX, interpolatedY, path2)) {
                            collisionDetected = true
                            gameStarted = false
                            touchX = startX
                            touchY = startY
                            break
                        } else if (interpolatedX >= width - radius) {
                            game?.endGame()
                            return true
                        }
                    }
                    invalidate()
                }
            }
            MotionEvent.ACTION_UP -> {
                gameStarted = false
                invalidate()
            }
        }
        return true
    }

    private fun checkCollision(x: Float, y: Float, path: List<Pair<Float, Float>>): Boolean {
        // Check if the touch point is close to the wire path
        for (i in 0 until path.size - 1) {
            val startX = path[i].first
            val startY = path[i].second
            val endX = path[i + 1].first
            val endY = path[i + 1].second

            val dist = distanceFromLineSegment(startX, startY, endX, endY, x, y)
            if (dist < radius) {
                return true
            }
        }
        return false
    }

    private fun distanceFromLineSegment(x1: Float, y1: Float, x2: Float, y2: Float, px: Float, py: Float): Float {
        val l2 = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1)
        if (l2 == 0f) return distance(px, py, x1, y1)
        var t = ((px - x1) * (x2 - x1) + (py - y1) * (y2 - y1)) / l2
        t = t.coerceIn(0f, 1f)
        return distance(px, py, x1 + t * (x2 - x1), y1 + t * (y2 - y1))
    }

    private fun distance(x1: Float, y1: Float, x2: Float, y2: Float): Float {
        return sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1))
    }
}
