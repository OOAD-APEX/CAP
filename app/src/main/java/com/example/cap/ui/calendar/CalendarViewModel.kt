package com.example.cap.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cap.database.dao.EventDAO
import java.time.YearMonth

class CalendarViewModel(private val dao: EventDAO) : ViewModel() {
    fun setMonthTitle(yearMonth: YearMonth) {
        title.value = "${yearMonth.year}\n${yearMonth.month.name}"
    }

    // create a MutableLiveData to store the title of the calendar
    // make it's setter private to prevent direct modification
    var title = MutableLiveData<String>("Not set")
        private set

}

class CalendarViewModelFactory(private val dao: EventDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java) ) {
            return CalendarViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}