package com.example.cap.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import com.example.cap.domain.Event

@Dao
interface EventDAO {

    @Update
    fun update(event: Event)

    @Insert
    fun insert(event: Event)

    @Delete
    fun delete(event: Event)

}