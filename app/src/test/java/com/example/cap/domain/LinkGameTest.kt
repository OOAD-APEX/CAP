package com.example.cap.domain

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import org.junit.After
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.kotlin.times
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
// BREAKING CHANGE!:
// android.graphic cant test with normal .jar, The real implementation is on the device.
// So we need Robolectric framework to mock the android class
// quote from https://stackoverflow.com/a/3175440
class LinkGameTest : LinkGame() {
    @Mock
    private lateinit var mockCanvas: Canvas

    @Mock
    private lateinit var mockPaint: Paint

    @Before
    fun setUp() {
        mockCanvas = mock(Canvas::class.java)
        mockPaint = mock(Paint::class.java)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testGenerateLetters() {
        super.generateLetters()

        assertTrue(leftLetters.size == linkCount)
        for (i in leftLetters) {
            assertTrue(i.isUpperCase())
        }

        assertTrue(rightLetters.size == linkCount)
        for (i in rightLetters) {
            assertTrue(i.isLetter())
        }

    }

    @Test
    fun testGeneratePoints() {
        val width = 100
        val height = 100
        super.generatePoints(width, height)

        assertTrue(startPoints.size == linkCount)
        assertTrue(endPoints.size == linkCount)

        for (point in startPoints) {
            assertTrue(point.x >= 0)
            assertTrue(point.y >= 0)
            // is at left side
            assertTrue(point.x <= width * 0.5)
        }

        for (point in endPoints) {
            assertTrue(point.x >= 0)
            assertTrue(point.y >= 0)
            // is at right side
            println(point.x)
            assertTrue(point.x >= width * 0.5)
        }

        // are they aligned?
        for (i in 0 until linkCount)
            assertEquals(startPoints[i].y, endPoints[i].y)
    }

    @Test
    fun testIsPointInCircle() {
        val x = 5f
        val y = 5f
        val circle = PointF(5f, 5f)

        assertTrue(isPointInCircle(x, y, circle))
    }

    @Test
    fun testIsValidConnection() {
        super.generateLetters()
        assertTrue(isValidConnection(0, 0))
        assertTrue(isValidConnection(1, 1))
        assertTrue(isValidConnection(2, 2))

        assertFalse(isValidConnection(-1, 0))
        assertFalse(isValidConnection(-1, 3))
        assertFalse(isValidConnection(0, -1))
        assertFalse(isValidConnection(3, 3))
    }

    @Test
    fun testIsMatchingLetters() {
        leftLetters = MutableList(3) { ' ' }
        rightLetters = MutableList(3) { ' ' }
        leftLetters[0] = 'A'
        rightLetters[0] = 'a'
        leftLetters[1] = 'B'
        rightLetters[1] = 'b'
        leftLetters[2] = 'C'
        rightLetters[2] = 'c'

        assertTrue(isMatchingLetters(0, 0))
        assertTrue(isMatchingLetters(1, 1))
        assertTrue(isMatchingLetters(2, 2))

        assertFalse(isMatchingLetters(0, 1))
        assertFalse(isMatchingLetters(1, 2))
        assertFalse(isMatchingLetters(2, 0))
    }

    @Test
    fun testResetSelection() {
        super.generateLetters()
        super.resetSelection()
        assertEquals(selectedStartIndex, -1)
        assertEquals(selectedEndIndex, -1)
        assertEquals(isDrawingLine, false)
    }

    @Test
    fun testisAlreadyConnected() {
        connections = mutableListOf()
        connections.add(Pair(0, 1))
        connections.add(Pair(1, 2))
        // not connected
        // connections.add(Pair(2, 0))

        assertTrue(isAlreadyConnected(0, true))
        assertTrue(isAlreadyConnected(1, true))
        assertFalse(isAlreadyConnected(2, true))

        assertFalse(isAlreadyConnected(0, false))
        assertTrue(isAlreadyConnected(1, false))
        assertTrue(isAlreadyConnected(2, false))

        //edge case
        assertThrows(IllegalArgumentException::class.java) {
            isAlreadyConnected(-1, true)
        }
        assertThrows(IllegalArgumentException::class.java) {
            isAlreadyConnected(-1, false)
        }
    }

    @Test
    fun testDrawLetterBackground() {
        super.generatePoints(100, 100)
        drawLetterBackground(mockCanvas, mockPaint)

        val invokeTimes = startPoints.size + endPoints.size

        verify(mockCanvas, times(invokeTimes)).drawCircle(any(Float::class.java), any(Float::class.java), eq(radius), eq(mockPaint))
    }

    @Test
    fun testDrawLetter() {
        // Arrange
        val mockCanvas = mock(Canvas::class.java)
        val mockPaint = mock(Paint::class.java)
        super.generateLetters()
        super.generatePoints(100, 100)

        // Assume
        val expectedInvokeTimes = startPoints.size + endPoints.size

        // Act
        drawLetter(mockCanvas, mockPaint)

        // Assert
        verify(mockCanvas, times(expectedInvokeTimes)).drawText(any(String::class.java), any(Float::class.java), any(Float::class.java), eq(mockPaint))
    }

}
