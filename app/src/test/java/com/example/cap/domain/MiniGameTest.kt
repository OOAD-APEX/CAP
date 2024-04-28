package com.example.cap.domain

import android.graphics.PointF
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.Ignore
import kotlin.math.min

class MiniGameTest : MiniGame() {
    @Before
    fun setUp() {
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
    @Ignore("The PointF is not testable")
    // android.jar. That JAR file is mostly stubs. The real implementation is on the device.
    // quote from https://stackoverflow.com/a/3175440
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
    @Ignore
    fun testIsPointInCircle() {
        val x = 5f
        val y = 5f
        val circle = PointF(5f, 5f)

        assertTrue(isPointInCircle(x, y, circle))
    }

    @Test
    fun isValidConnection() {
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
}