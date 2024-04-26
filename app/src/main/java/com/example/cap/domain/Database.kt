package com.example.cap.domain

import androidx.room.Database
import com.example.cap.domain.dao.EventDAO
import com.example.cap.domain.data.Event

@Database(
    entities = [
        // add your data class here
        Event::class
    ],
    version = 1
)
abstract class Database {
    // add your DAO here
    abstract fun eventDao(): EventDAO

}