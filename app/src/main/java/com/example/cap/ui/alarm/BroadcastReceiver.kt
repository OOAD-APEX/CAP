package com.example.cap.ui.alarm
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.app.NotificationManager
import android.app.NotificationChannel
import android.app.PendingIntent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.cap.R
import android.media.RingtoneManager
import android.media.Ringtone
import com.example.cap.domain.Alarm
import com.example.cap.ui.alarm.AlarmReceiver.Companion.stopRingtone

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        var ringtone: Ringtone? = null
        fun stopRingtone() {
            ringtone?.stop()
        }

    }



    private val alarm = Alarm()

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == "STOP_ALARM") {
            alarm.stopRingtoneAndNotification(context)
        }
        else {
            alarm.startRingtoneAndNotification(context)
        }
    }
}

// Glossary
// cancel alarm(play game) -> start game
// actually turn off -> cancel alarm