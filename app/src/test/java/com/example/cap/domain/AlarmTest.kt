package com.example.cap.domain

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.robolectric.RobolectricTestRunner
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
class AlarmTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockCalendar: Calendar

    @Mock
    private lateinit var mockAlarmManager: AlarmManager

    @Mock
    private lateinit var mockNotificationManager: NotificationManager

    private lateinit var alarm: Alarm

    @Before
    fun setUp() {
        mockContext = mock(Context::class.java)
        mockCalendar = mock(Calendar::class.java)
        mockAlarmManager = mock(AlarmManager::class.java)
        mockNotificationManager = mock(NotificationManager::class.java)
        alarm = Alarm()
    }

    @After
    fun tearDown() {
    }

    @Test
    fun testSetAlarm() {
        `when`(mockContext.getSystemService(Context.ALARM_SERVICE)).thenReturn(mockAlarmManager)
        alarm.setAlarm(mockContext, mockCalendar)
        verify(mockAlarmManager).setExact(anyInt(), anyLong(), any())
    }

    @Test
    fun testCancelAlarm() {
        alarm.cancelAlarm(mockContext)
        verify(mockContext).sendBroadcast(any(Intent::class.java))
    }


    @Test
    fun testStopRingtoneAndNotification() {
        `when`(mockContext.getSystemService(Context.NOTIFICATION_SERVICE)).thenReturn(mockNotificationManager)
        alarm.stopRingtoneAndNotification(mockContext)
        verify(mockNotificationManager).cancel(anyInt())
    }


    @Test
    fun testDeleteAlarm() {
        `when`(mockContext.getSystemService(Context.ALARM_SERVICE)).thenReturn(mockAlarmManager)
        alarm.deleteAlarm(mockContext)
        verify(mockAlarmManager).cancel(any(PendingIntent::class.java))
    }

    @Test
    fun testUpdateAlarm() {
        `when`(mockContext.getSystemService(Context.ALARM_SERVICE)).thenReturn(mockAlarmManager)
        `when`(mockCalendar.timeInMillis).thenReturn(1000L)
        alarm.updateAlarm(mockContext, mockCalendar)
        verify(mockAlarmManager).setExact(eq(AlarmManager.RTC_WAKEUP), eq(1000L), any())
    }
}