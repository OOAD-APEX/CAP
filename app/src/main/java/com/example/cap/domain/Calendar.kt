package com.example.cap.domain

import com.example.cap.MainApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class Calendar {
    private val eventDao = MainApplication.database.eventDao()

    suspend fun saveEvent (event: Event): Long{
        return withContext(Dispatchers.IO) {
            val eventId = eventDao.insert(event)
            eventId
        }
    }

    suspend fun deleteEvent(event: Event) {
        eventDao.delete(event)
    }

    suspend fun deleteEventById(id: Int) {
        eventDao.deleteById(id)
    }

    suspend fun updateEvent(event: Event) {
        eventDao.update(event)
    }

    fun getEvents(id: Int) : Flow<Event> {
        return eventDao.getItem(id)
    }

    fun getAllItems(): Flow<List<Event>> {
        return eventDao.getAllItems()
    }

    fun getAllItemsForNormalList(): List<Event> {
        return eventDao.getAllItemsForNormalList()
    }
}