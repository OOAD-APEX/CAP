package com.example.cap.domain.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import com.example.cap.domain.data.Event

@Dao
interface EventDAO {

    @Insert
    fun insert(event: Event)

    @Delete
    fun delete(event: Event)

}