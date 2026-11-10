package com.example.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.R
import com.example.data.db.AlarmDatabase
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val alarmId = intent.getLongExtra("ALARM_ID", -1L)
        val label = intent.getStringExtra("ALARM_LABEL") ?: "AuraClock Alarm"
        val soundTone = intent.getStringExtra("ALARM_SOUND") ?: "System Default"
        val soundUri = intent.getStringExtra("ALARM_SOUND_URI")
        val vibrate = intent.getBooleanExtra("ALARM_VIBRATE", true)
        val snooze = intent.getIntExtra("ALARM_SNOOZE", 5)

        // If boot completed, reschedule all enabled alarms
        if (Intent.ACTION_BOOT_COMPLETED == intent.action) {
            val db = Room.databaseBuilder(context, AlarmDatabase::class.java, "auraclock_db")
                .fallbackToDestructiveMigration()
                .build()
            CoroutineScope(Dispatchers.IO).launch {
                val alarms = db.alarmDao().getEnabledAlarms()
                for (alarm in alarms) {
                    AlarmScheduler.scheduleAlarm(context, alarm)
                }
            }
            return
        }

        // Start alert sound & vibration with auto-stop
        AlarmSoundPlayer.playSound(context, soundUri, soundTone, vibrate, autoStopSeconds = 90L)

        // Launch AlarmAlertActivity fullscreen
        val alertIntent = Intent(context, AlarmAlertActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("ALARM_ID", alarmId)
            putExtra("ALARM_LABEL", label)
            putExtra("ALARM_SNOOZE", snooze)
            putExtra("ALARM_SOUND", soundTone)
            putExtra("ALARM_VIBRATE", vibrate)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            alarmId.toInt(),
            alertIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "auraclock_alarms"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                context.getString(R.string.alarm_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = context.getString(R.string.alarm_channel_desc)
                enableVibration(true)
                setBypassDnd(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_clock_1794253815250)
            .setContentTitle("⏰ $label")
            .setContentText("Tap to dismiss or snooze")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(alarmId.toInt(), notification)

        // Try direct launch
        try {
            context.startActivity(alertIntent)
        } catch (e: Exception) {
            // Already handled via full-screen intent notification
        }
    }
}
