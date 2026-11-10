package com.example.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.widget.RemoteViews
import androidx.compose.ui.graphics.toArgb
import androidx.room.Room
import com.example.MainActivity
import com.example.R
import com.example.data.UserPreferencesRepository
import com.example.data.db.AlarmDatabase
import com.example.data.model.ClockTheme
import com.example.data.model.WatchFaceType
import com.example.speech.TimeSpeaker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.min

class AuraClockWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            "com.example.ACTION_SPEAK_TIME" -> {
                val prefsRepo = UserPreferencesRepository(context)
                val is24 = prefsRepo.preferences.value.is24Hour
                val db = Room.databaseBuilder(context, AlarmDatabase::class.java, "auraclock_db")
                    .fallbackToDestructiveMigration()
                    .build()
                CoroutineScope(Dispatchers.IO).launch {
                    val alarms = db.alarmDao().getEnabledAlarms()
                    val nextAlarmText = if (alarms.isNotEmpty()) {
                        "Next alarm is at ${alarms.first().formattedTime(is24)}."
                    } else null
                    TimeSpeaker.getInstance(context).speakTime(is24, nextAlarmText)
                }
            }
            "com.example.ACTION_TOGGLE_NIGHTSTAND" -> {
                val nightstandIntent = Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                    putExtra("START_NIGHTSTAND", true)
                }
                context.startActivity(nightstandIntent)
            }
        }
    }

    companion object {
        fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
            val views = RemoteViews(context.packageName, R.layout.widget_auraclock)
            val prefsRepo = UserPreferencesRepository(context)
            val prefs = prefsRepo.preferences.value
            val theme = prefs.effectiveTheme()

            // Date text
            val dateFormat = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
            views.setTextViewText(R.id.widget_date_text, dateFormat.format(Date()).uppercase(Locale.getDefault()))
            views.setTextViewText(R.id.widget_seconds_or_motto, prefs.selectedWatchFace.title.uppercase(Locale.getDefault()))

            // Render Analogue Watch Face
            val bitmap = renderAnalogWatchFaceBitmap(context, prefs.selectedWatchFace, theme, 260, 260)
            views.setImageViewBitmap(R.id.widget_analog_face, bitmap)

            // Intent to open Main App
            val openAppIntent = Intent(context, MainActivity::class.java)
            val openPendingIntent = PendingIntent.getActivity(
                context,
                0,
                openAppIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_clock_container, openPendingIntent)

            // Intent to speak time
            val speakIntent = Intent(context, AuraClockWidgetProvider::class.java).apply {
                action = "com.example.ACTION_SPEAK_TIME"
            }
            val speakPendingIntent = PendingIntent.getBroadcast(
                context,
                101,
                speakIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_speak, speakPendingIntent)

            // Intent to launch nightstand mode
            val nightstandIntent = Intent(context, AuraClockWidgetProvider::class.java).apply {
                action = "com.example.ACTION_TOGGLE_NIGHTSTAND"
            }
            val nightstandPendingIntent = PendingIntent.getBroadcast(
                context,
                102,
                nightstandIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_btn_nightstand, nightstandPendingIntent)

            // Check next alarm asynchronously
            val db = Room.databaseBuilder(context, AlarmDatabase::class.java, "auraclock_db")
                .fallbackToDestructiveMigration()
                .build()
            CoroutineScope(Dispatchers.IO).launch {
                val alarms = db.alarmDao().getEnabledAlarms()
                val alarmText = if (alarms.isNotEmpty()) {
                    "⏰ " + alarms.first().formattedTime(prefs.is24Hour)
                } else {
                    "⏰ None"
                }
                views.setTextViewText(R.id.widget_alarm_text, alarmText)
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        private fun renderAnalogWatchFaceBitmap(
            context: Context,
            watchFace: WatchFaceType,
            theme: ClockTheme,
            widthPx: Int = 260,
            heightPx: Int = 260
        ): Bitmap {
            val bitmap = Bitmap.createBitmap(widthPx, heightPx, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)

            val cx = widthPx / 2f
            val cy = heightPx / 2f
            val radius = (min(widthPx, heightPx) / 2f) - 4f

            // Map watch face to authentic drawable image
            val drawableResId = when (watchFace) {
                WatchFaceType.CYBER_CARVED_3D -> R.drawable.face_cyber_3d
                WatchFaceType.MOTORSPORT_M -> R.drawable.face_motorsport_m
                WatchFaceType.SPEEDMASTER_RACING -> R.drawable.face_speedmaster_racing
                WatchFaceType.RACING_YELLOW_TACHY -> R.drawable.face_racing_yellow
                WatchFaceType.CASIO_ROYALE -> R.drawable.face_casio_royale
                WatchFaceType.HAWKER_AVIATOR -> R.drawable.face_hawker_hunter
                WatchFaceType.MECHA_TACTICAL -> R.drawable.face_mecha_tactical
                WatchFaceType.SEIKO_DIGI_ANA -> R.drawable.face_seiko_digiana
                WatchFaceType.CLUB_RETROGRADE -> R.drawable.face_club_retrograde
                WatchFaceType.ROTARY_GAUGE -> R.drawable.face_rotary_gauge
                WatchFaceType.BMW_M_WHEEL -> R.drawable.face_bmw_wheel
                WatchFaceType.ORANGE_TACTICAL_DIVER -> R.drawable.face_orange_diver
                WatchFaceType.COCKPIT_YELLOW -> R.drawable.face_cockpit_yellow
                WatchFaceType.TITANIUM_RED_CHRONO -> R.drawable.face_titanium_red
                WatchFaceType.MODERN_PODS_SPORTS -> R.drawable.face_sports_pods
                else -> null
            }

            if (drawableResId != null) {
                try {
                    val rawBmp = android.graphics.BitmapFactory.decodeResource(context.resources, drawableResId)
                    if (rawBmp != null) {
                        val path = android.graphics.Path().apply {
                            addCircle(cx, cy, radius, android.graphics.Path.Direction.CW)
                        }
                        canvas.save()
                        canvas.clipPath(path)
                        val srcRect = android.graphics.Rect(0, 0, rawBmp.width, rawBmp.height)
                        val dstRect = android.graphics.RectF(cx - radius, cy - radius, cx + radius, cy + radius)
                        canvas.drawBitmap(rawBmp, srcRect, dstRect, paint)
                        canvas.restore()
                    }
                } catch (_: Exception) {}
            } else {
                // Dial Background fallback
                paint.style = Paint.Style.FILL
                paint.color = 0xFF080D18.toInt()
                canvas.drawCircle(cx, cy, radius, paint)

                // Outer Bezel Ring
                paint.style = Paint.Style.STROKE
                paint.strokeWidth = 4f
                paint.color = theme.primaryColor.toArgb()
                canvas.drawCircle(cx, cy, radius - 2f, paint)

                // Hour Ticks
                val tickPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = theme.accentColor.toArgb()
                    strokeWidth = 3f
                    strokeCap = Paint.Cap.ROUND
                }
                for (h in 1..12) {
                    val angleRad = (h * 30.0 - 90.0) * (Math.PI / 180.0)
                    val outerX = cx + (radius - 6f) * Math.cos(angleRad).toFloat()
                    val outerY = cy + (radius - 6f) * Math.sin(angleRad).toFloat()
                    val isQuarter = h % 3 == 0
                    val tickLen = if (isQuarter) 14f else 7f
                    val innerX = cx + (radius - 6f - tickLen) * Math.cos(angleRad).toFloat()
                    val innerY = cy + (radius - 6f - tickLen) * Math.sin(angleRad).toFloat()
                    tickPaint.strokeWidth = if (isQuarter) 3.5f else 1.8f
                    canvas.drawLine(innerX, innerY, outerX, outerY, tickPaint)
                }
            }

            // Current Time Hands overlaid
            val cal = Calendar.getInstance()
            val hour = cal.get(Calendar.HOUR)
            val minute = cal.get(Calendar.MINUTE)
            val second = cal.get(Calendar.SECOND)

            val minAngle = (minute + second / 60.0) * 6.0
            val hrAngle = (hour + minute / 60.0) * 30.0

            // Hour Hand
            val hourPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xFFFFFFFF.toInt()
                strokeWidth = 7f
                strokeCap = Paint.Cap.ROUND
                setShadowLayer(4f, 2f, 2f, 0xAA000000.toInt())
            }
            val hRad = (hrAngle - 90.0) * (Math.PI / 180.0)
            val hLen = radius * 0.48f
            canvas.drawLine(cx, cy, cx + hLen * Math.cos(hRad).toFloat(), cy + hLen * Math.sin(hRad).toFloat(), hourPaint)

            // Minute Hand
            val minPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = theme.primaryColor.toArgb()
                strokeWidth = 4.5f
                strokeCap = Paint.Cap.ROUND
                setShadowLayer(4f, 2f, 2f, 0xAA000000.toInt())
            }
            val mRad = (minAngle - 90.0) * (Math.PI / 180.0)
            val mLen = radius * 0.72f
            canvas.drawLine(cx, cy, cx + mLen * Math.cos(mRad).toFloat(), cy + mLen * Math.sin(mRad).toFloat(), minPaint)

            // Center Pin
            paint.style = Paint.Style.FILL
            paint.color = theme.accentColor.toArgb()
            canvas.drawCircle(cx, cy, 5.5f, paint)
            paint.color = 0xFF000000.toInt()
            canvas.drawCircle(cx, cy, 2.5f, paint)

            return bitmap
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName = ComponentName(context, AuraClockWidgetProvider::class.java)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            for (id in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, id)
            }
        }
    }
}
