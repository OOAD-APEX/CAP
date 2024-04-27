package com.example.cap

import com.example.cap.ui.fortune.DailyFortuneViewModel
import org.junit.Assert.*
import org.junit.Test

class DailyFortuneTest {
    @Test
    fun testGetRandomFortuneColor() {
        val dailyFortuneViewModel = DailyFortuneViewModel()
        val fortuneColors = listOf("紅", "橘", "黃", "綠", "藍", "紫", "粉", "黑", "白")
        repeat(50) {
            assertTrue(fortuneColors.contains(dailyFortuneViewModel.getRandomFortuneColor()))
        }
    }
    @Test
    fun testGetRandomDailyFortune() {
        val dailyFortuneViewModel = DailyFortuneViewModel()
        val dailyFortunes = listOf("大吉", "中吉", "小吉", "普通")
        repeat(50) {
            assertTrue(dailyFortunes.contains(dailyFortuneViewModel.getRandomDailyFortune()))
        }
    }
}
