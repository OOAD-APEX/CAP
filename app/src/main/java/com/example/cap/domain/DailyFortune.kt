package com.example.cap.domain

class DailyFortune {
    fun getRandomFortuneColor(): String {
        val fortuneColors = listOf("紅", "橘", "黃", "綠", "藍", "紫", "粉", "黑", "白")
        return fortuneColors.random()
    }

    fun getRandomDailyFortune(): String {
        val dailyFortunes = listOf("大吉",
            "中吉",
            "小吉",
            "普通")
        return dailyFortunes.random()
    }
}
