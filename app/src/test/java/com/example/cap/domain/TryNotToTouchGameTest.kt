package com.example.cap.domain

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.MotionEvent
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TryNotToTouchGameTest : TryNotToTouchGame() {
    @Mock
    private lateinit var mockCanvas: Canvas

    @Mock
    private lateinit var mockPaint: Paint

    @Before
    fun setUp() {
        mockCanvas = mock(Canvas::class.java)
        mockPaint = mock(Paint::class.java)
        setScreenSize(1000, 2000)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testGenerateWalls() {
        generateWalls(1000, 2000)
        assertEquals(4, path1.size)
        assertEquals(4, path2.size)
        assertEquals(Pair(0f, 400f), path1[0])
        assertEquals(Pair(600f, 1400f), path1[2])
        assertEquals(Pair(300f, 700f), path2[1])
        assertEquals(Pair(1000f, 1700f), path2[3])
    }

    @Test
    fun testDrawWalls() {
        generateWalls(1000, 2000)
        drawWalls(mockCanvas, mockPaint, mockPaint)
        verify(mockCanvas, times(2)).drawPath(any(Path::class.java), eq(mockPaint))
    }

    @Test
    fun testDrawTouchPoint() {
        drawTouchPoint(mockCanvas, mockPaint)
        val radius = 80f
        verify(mockCanvas).drawCircle(startX, startY, radius, mockPaint)
    }

    @Test
    fun testCheckCollision() {
        generateWalls(1000, 2000)
        val collision = checkCollision(path1[1].first, path1[1].second, path1)
        assertTrue(collision)
    }

    @Test
    fun testDistance() {
        val dist = distance(0f, 0f, 3f, 4f)
        assertEquals(5f, dist, 0.001f)
    }

    @Test
    fun testDistanceFromLineSegment() {
        val dist = distanceFromLineSegment(0f, 0f, 10f, 0f, 5f, 5f)
        assertEquals(5f, dist, 0.001f)
    }
}
