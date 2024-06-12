package com.example.cap.domain

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.cap.R
import com.example.cap.ui.alarm.AlarmActivity
import com.example.cap.ui.alarm.AlarmReceiver
import java.util.Calendar
import com.example.cap.ui.fortune.DailyFortuneDialog

class Alarm {
    fun setAlarm(context: Context, cal: Calendar, intentId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.putExtra("alarmTime", cal.timeInMillis)
        intent.action = "START_ALARM"
        val pendingIntent = PendingIntent.getBroadcast(context, intentId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        // if alarmtime<currenttime, set alarm for next day
        if (cal.timeInMillis < System.currentTimeMillis()) {
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.timeInMillis, pendingIntent)
    }

    fun setAlarm(context: Context, cal: Calendar) {
        setAlarm(context, cal, 0)
    }

    fun cancelAlarm(context: Context) {
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.action = "STOP_ALARM"
        context.sendBroadcast(intent)
    }

    fun deleteAlarm(context: Context, intentId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        intent.action = "START_ALARM"
        val pendingIntent = PendingIntent.getBroadcast(context, intentId, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        alarmManager.cancel(pendingIntent)
    }

    fun deleteAlarm(context: Context) {
        deleteAlarm(context, 0)
    }

    fun updateAlarm(context: Context, cal: Calendar, intentId: Int) {
        deleteAlarm(context, intentId)
        setAlarm(context, cal)
    }

    fun replaceTheOnlyAlarm(context: Context, cal: Calendar) {
        setAlarm(context, cal)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun startRingtoneAndNotification(context: Context) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val ringtoneUriString = sharedPreferences.getString("selected_ringtone_uri", null)
        val alarmUri = if (ringtoneUriString != null) Uri.parse(ringtoneUriString) else RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        AlarmReceiver.ringtone = RingtoneManager.getRingtone(context, alarmUri)
        AlarmReceiver.ringtone?.apply {
            isLooping = true
            play()
        }
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("default", "Default", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, "default")
            .setContentTitle("Alarm")
            .setContentText("The alarm is ringing.")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        notificationManager.notify(1, notification)
        val intent = Intent(context, AlarmActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun stopRingtoneAndNotification(context: Context) {
        AlarmReceiver.stopRingtone()
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
    }

    fun onGameComplete(context: Context) {
        cancelAlarm(context)
        val dialog = DailyFortuneDialog(context)
        dialog.show()
        // close the activity
        dialog.setOnDismissListener {
            (context as Activity).finish()
        }
    }
}

