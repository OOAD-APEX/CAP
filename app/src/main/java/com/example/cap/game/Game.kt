package com.example.cap.game

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

interface Game {
    fun onCreate(savedInstanceState: Bundle?) {
    }

    fun startGame()
    fun endGame()

}