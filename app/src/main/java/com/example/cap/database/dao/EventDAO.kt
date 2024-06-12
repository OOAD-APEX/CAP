package com.example.cap.database.dao

import androidx.room.*
import com.example.cap.domain.Event
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(event: Event): Long

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun delete(event: Event)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * from events WHERE id = :id")
    fun getItem(id: Int): Flow<Event>

    @Query("SELECT * from events ORDER BY time ASC")
    fun getAllItems(): Flow<List<Event>>

    @Query("SELECT * from events ORDER BY time ASC")
    fun getAllItemsForNormalList(): List<Event>
}