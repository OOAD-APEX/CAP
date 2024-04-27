package com.example.cap.ui.alarm
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.cap.R
import android.media.RingtoneManager
import android.media.Ringtone
import com.example.cap.ui.alarm.AlarmReceiver.Companion.stopRingtone

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        var ringtone: Ringtone? = null
        fun stopRingtone() {
            ringtone?.stop()
        }

    }
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "STOP_ALARM") {
            stopRingtoneAndNotification(context)
        } else {
            startRingtoneAndNotification(context)
        }
    }

    private fun startRingtoneAndNotification(context: Context) {
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        Companion.ringtone = RingtoneManager.getRingtone(context, alarmUri)
        Companion.ringtone?.play()
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
    // Your existing code to start the ringtone and notification
}

private fun stopRingtoneAndNotification(context: Context) {
    stopRingtone()
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(1)
}