package com.example.cap.ui.calendar

import androidx.lifecycle.*
import com.example.cap.MainApplication
import com.example.cap.database.dao.EventDAO
import com.example.cap.domain.Event
import com.example.cap.domain.TriggerMode
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.YearMonth

class CalendarViewModel() : ViewModel() {
    val eventDao = MainApplication.database.eventDao()


    fun setMonthTitle(yearMonth: YearMonth) {
        title.value = "${yearMonth.year}\n${yearMonth.month.name}"
        viewModelScope.launch {
            eventDao.insert(Event(0, LocalDateTime.now(), TriggerMode.ALARM, "asd"))
        }
    }

    fun saveEvent(text: String, time: LocalDateTime, triggerMode: TriggerMode) {
        val event = Event(0, time, triggerMode, text)

        viewModelScope.launch {
            eventDao.insert(event)
        }
    }

    // create a MutableLiveData to store the title of the calendar
    // make it's setter private to prevent direct modification
    var title = MutableLiveData<String>("Not set")
        private set

}