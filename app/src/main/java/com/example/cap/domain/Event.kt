package com.example.cap.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "events")
data class Event(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "time")
    val time: LocalDateTime,
    @ColumnInfo(name = "trigger_mode")
    val triggerMode: TriggerMode,
    @ColumnInfo(name = "title")
    val title: String,
)
