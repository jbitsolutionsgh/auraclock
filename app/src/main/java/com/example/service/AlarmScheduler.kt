package com.example.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.db.AlarmEntity
import java.util.Calendar

object AlarmScheduler {

    fun scheduleAlarm(context: Context, alarm: AlarmEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        if (!alarm.isEnabled) {
            cancelAlarm(context, alarm.id)
            return
        }

        val triggerTime = calculateNextTriggerMillis(alarm)

        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("ALARM_ID", alarm.id)
            putExtra("ALARM_LABEL", alarm.label)
            putExtra("ALARM_SOUND", alarm.soundTone)
            putExtra("ALARM_SOUND_URI", alarm.soundUri)
            putExtra("ALARM_VIBRATE", alarm.vibrate)
            putExtra("ALARM_SNOOZE", alarm.snoozeMinutes)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarm.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                } else {
                    alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
            Log.d("AlarmScheduler", "Alarm ${alarm.id} scheduled for $triggerTime")
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Failed to schedule alarm ${alarm.id}", e)
        }
    }

    fun cancelAlarm(context: Context, alarmId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            alarmId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    fun calculateNextTriggerMillis(alarm: AlarmEntity): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, alarm.hour)
            set(Calendar.MINUTE, alarm.minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // If today's time has already passed, check next days
        if (target.before(now) || target.timeInMillis == now.timeInMillis) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }

        // If specific days of week are selected (mask != 0 and mask != 127)
        if (alarm.daysOfWeekMask != 0 && alarm.daysOfWeekMask != 127) {
            // Find next matching day (up to 7 days search)
            for (i in 0..7) {
                val dayOfWeek = target.get(Calendar.DAY_OF_WEEK)
                if (alarm.isDayActive(dayOfWeek)) {
                    break
                }
                target.add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        return target.timeInMillis
    }
}
