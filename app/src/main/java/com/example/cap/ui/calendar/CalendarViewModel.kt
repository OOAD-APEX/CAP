package com.example.cap.ui.calendar

import androidx.lifecycle.*
import com.example.cap.MainApplication
import com.example.cap.database.dao.EventDAO
import com.example.cap.domain.Calendar
import com.example.cap.domain.Event
import com.example.cap.domain.TriggerMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth

class CalendarViewModel() : ViewModel() {
    private val eventDao = MainApplication.database.eventDao()
    private val calendar = Calendar()
    val events = eventDao.getAllItems().asLiveData()
    var selectedDate: MutableLiveData<LocalDate> = MutableLiveData(LocalDate.now())

    fun setMonthTitle(yearMonth: YearMonth) {
        title.value = "${yearMonth.year}\n${yearMonth.month.name}"
    }

    fun saveEvent(text: String, time: LocalDateTime, triggerMode: TriggerMode): LiveData<Long>{
        val id = MutableLiveData<Long>()
        viewModelScope.launch {
            val event = Event(0, time, triggerMode, text)
            id.value = calendar.saveEvent(event)
        }
        return id
    }

    fun updateEvent(id: Int, text: String, time: LocalDateTime, triggerMode: TriggerMode) {
        viewModelScope.launch {
            val event = Event(id, time, triggerMode, text)
            calendar.updateEvent(event)
        }
    }

    // create a MutableLiveData to store the title of the calendar
    // make it's setter private to prevent direct modification
    var title = MutableLiveData<String>("Not set")
        private set

}