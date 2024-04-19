package com.example.cap.ui.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.YearMonth

class CalendarViewModel : ViewModel() {
    fun setMonthTitle(yearMonth: YearMonth) {
        title.value = "${yearMonth.year}\n${yearMonth.month.name}"
    }

    // create a MutableLiveData to store the title of the calendar
    // make it's setter private to prevent direct modification
    var title = MutableLiveData<String>("Not set")
        private set

}