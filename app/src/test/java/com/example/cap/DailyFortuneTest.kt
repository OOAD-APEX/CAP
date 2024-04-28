package com.example.cap

import com.example.cap.domain.DailyFortune
import org.junit.Assert.*
import org.junit.Test

class DailyFortuneTest {
    @Test
    fun testGetRandomFortuneColor() {
        val dailyFortune = DailyFortune()
        val fortuneColors = listOf("紅", "橘", "黃", "綠", "藍", "紫", "粉", "黑", "白")
        repeat(50) {
            assertTrue(fortuneColors.contains(dailyFortune.getRandomFortuneColor()))
        }
    }
    @Test
    fun testGetRandomDailyFortune() {
        val dailyFortune = DailyFortune()
        val dailyFortunes = listOf("大吉", "中吉", "小吉", "普通")
        repeat(50) {
            assertTrue(dailyFortunes.contains(dailyFortune.getRandomDailyFortune()))
        }
    }
}
