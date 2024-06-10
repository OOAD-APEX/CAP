package com.example.cap.domain
import com.example.cap.game.Game
import android.graphics.*
import android.view.MotionEvent
import kotlin.math.sqrt

open class TryNotToTouchGame {
    private var path1 = mutableListOf<Pair<Float, Float>>()
    private var path2 = mutableListOf<Pair<Float, Float>>()
    private var touchX = 0f
    private var touchY = 0f
    private var collisionDetected = false
    private var game: Game? = null
    var gameStarted = false

    private val radius = 80f  //觸碰半徑
    private var startX = 0f
    private var startY = 0f
    protected var width = 0
    protected var height = 0
    fun setGame(game: Game) {
        this.game = game
    }

    fun generateWalls(w: Int, h: Int) {
        startX = w * 0.05f
        startY = h * 0.275f

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
    }

    fun setScreenSize(w: Int, h: Int) {
        width = w
        height = h
    }

    fun drawWalls(canvas: Canvas, blackPaint: Paint, wirePaint: Paint) {
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
        drawPath(canvas, path1, wirePaint)

        // Draw the second wire
        drawPath(canvas, path2, wirePaint)
    }

    fun drawTouchPoint(canvas: Canvas, touchPaint: Paint) {
        // Draw the touch point
        if (gameStarted) {
            canvas.drawCircle(touchX, touchY, radius, touchPaint)
        } else {
            // Draw start point
            canvas.drawCircle(startX, startY, radius, touchPaint)
        }
    }

    fun drawCollisionMessage(canvas: Canvas, collisionPaint: Paint) {
        // Draw collision message
        if (collisionDetected) {
            canvas.drawText("清醒點!", width * 0.5f, height * 0.15f, collisionPaint)
        }
    }


    private fun drawPath(canvas: Canvas, path: List<Pair<Float, Float>>, wirePaint: Paint) {
        for (i in 0 until path.size - 1) {
            val startX = path[i].first
            val startY = path[i].second
            val endX = path[i + 1].first
            val endY = path[i + 1].second
            canvas.drawLine(startX, startY, endX, endY, wirePaint)
        }
    }

    fun handleTouchEvent(event: MotionEvent, invalidateView: () -> Unit): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!gameStarted && distance(event.x, event.y, startX, startY) <= radius) {
                    touchX = event.x
                    touchY = event.y
                    gameStarted = true
                    collisionDetected = false
                    invalidateView()
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
                    invalidateView()
                }
            }
            MotionEvent.ACTION_UP -> {
                gameStarted = false
                invalidateView()
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

