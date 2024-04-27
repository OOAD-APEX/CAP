package com.example.cap.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cap.database.dao.EventDAO
import com.example.cap.domain.Event

@Database(
    entities = [
        // add your data class here
        Event::class
    ],
    version = 1
)

@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    // add your DAO here
    abstract fun eventDao(): EventDAO

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            if (INSTANCE == null) {
                // synchronized means only one thread can access this block at same time
                synchronized(this) {
                    INSTANCE = Room.databaseBuilder(
                        context,
                        AppDatabase::class.java,
                        "app_database"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }

}