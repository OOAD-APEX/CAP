package com.example.cap.domain

import android.app.Application
import android.content.Context
import java.util.Calendar
class Calendar {
    fun dsf () {
        // get the application context
        val context = Application()
    }

    class AlarmListManager(private val context: Context) {
        private val alarms = mutableListOf<Alarm>()

        fun addAlarm(cal: Calendar) {
            val alarm = Alarm()
            alarm.setAlarm(context, cal)
            alarms.add(alarm)
        }

        fun cancelAlarm(index: Int) {
            val alarm = alarms[index]
            alarm.cancelAlarm(context)
            alarms.removeAt(index)
        }

        fun updateAlarm(index: Int, cal: Calendar) {
            val alarm = alarms[index]
            alarm.cancelAlarm(context)
            alarm.setAlarm(context, cal)
        }

        fun getAlarms(): List<Alarm> {
            return alarms
        }
    }
}