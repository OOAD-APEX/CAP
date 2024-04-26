package com.example.cap.domain.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.cap.domain.TriggerMode
import java.time.LocalDateTime

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "time")
    val time: LocalDateTime,
    @ColumnInfo(name = "trigger_mode")
    val triggerMode: TriggerMode,
    @ColumnInfo(name = "title")
    val title: Int,
    @ColumnInfo(name = "description")
    val description: Int
)
