package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.view.KeyEvent
import androidx.core.app.NotificationCompat
import androidx.room.Room
import com.example.MainActivity
import com.example.R
import com.example.data.UserPreferencesRepository
import com.example.data.db.AlarmDatabase
import com.example.data.model.EarbudAction
import com.example.speech.TimeSpeaker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class EarbudMediaService : Service() {
    private var mediaSession: MediaSession? = null
    private lateinit var prefsRepo: UserPreferencesRepository
    private var audioManager: AudioManager? = null
    private var audioFocusRequest: AudioFocusRequest? = null

    override fun onCreate() {
        super.onCreate()
        prefsRepo = UserPreferencesRepository(this)
        requestEarbudAudioFocus()
        setupMediaSession()
        startForegroundNotification()
    }

    private fun requestEarbudAudioFocus() {
        audioManager = getSystemService(Context.AUDIO_SERVICE) as? AudioManager
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val playbackAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()
                audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(playbackAttributes)
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener { focusChange ->
                        Log.d(TAG, "Audio focus changed: $focusChange")
                    }
                    .build()
                audioFocusRequest?.let { audioManager?.requestAudioFocus(it) }
            } else {
                @Suppress("DEPRECATION")
                audioManager?.requestAudioFocus(
                    null,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed requesting audio focus", e)
        }
    }

    private fun setupMediaSession() {
        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON, null, this, EarbudMediaButtonReceiver::class.java)
        val pendingMediaButton = PendingIntent.getBroadcast(
            this,
            0,
            mediaButtonIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        mediaSession = MediaSession(this, "AuraClockMediaSession").apply {
            setMediaButtonReceiver(pendingMediaButton)

            setCallback(object : MediaSession.Callback() {
                override fun onMediaButtonEvent(mediaButtonIntent: Intent): Boolean {
                    val keyEvent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT, KeyEvent::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
                    }

                    if (keyEvent != null && keyEvent.action == KeyEvent.ACTION_DOWN) {
                        return handleKey(keyEvent.keyCode)
                    }
                    return super.onMediaButtonEvent(mediaButtonIntent)
                }

                override fun onPlay() {
                    executeAction(this@EarbudMediaService, prefsRepo.preferences.value.earbudSinglePress)
                }

                override fun onPause() {
                    executeAction(this@EarbudMediaService, prefsRepo.preferences.value.earbudSinglePress)
                }

                override fun onSkipToNext() {
                    executeAction(this@EarbudMediaService, prefsRepo.preferences.value.earbudDoublePress)
                }

                override fun onSkipToPrevious() {
                    executeAction(this@EarbudMediaService, prefsRepo.preferences.value.earbudTriplePress)
                }
            })

            val state = PlaybackState.Builder()
                .setActions(
                    PlaybackState.ACTION_PLAY or
                            PlaybackState.ACTION_PAUSE or
                            PlaybackState.ACTION_PLAY_PAUSE or
                            PlaybackState.ACTION_SKIP_TO_NEXT or
                            PlaybackState.ACTION_SKIP_TO_PREVIOUS
                )
                .setState(PlaybackState.STATE_PLAYING, 0, 1.0f)
                .build()
            setPlaybackState(state)
            isActive = true
        }
    }

    private fun handleKey(keyCode: Int): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_HEADSETHOOK,
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
            KeyEvent.KEYCODE_MEDIA_PLAY,
            KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                handleMultiClickDirect(this@EarbudMediaService)
                return true
            }
            KeyEvent.KEYCODE_MEDIA_NEXT,
            KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                executeAction(this@EarbudMediaService, prefsRepo.preferences.value.earbudDoublePress)
                return true
            }
            KeyEvent.KEYCODE_MEDIA_PREVIOUS,
            KeyEvent.KEYCODE_MEDIA_REWIND -> {
                executeAction(this@EarbudMediaService, prefsRepo.preferences.value.earbudTriplePress)
                return true
            }
        }
        return false
    }

    private fun startForegroundNotification() {
        val channelId = "auraclock_earbud_channel"
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                getString(R.string.earbud_service_channel),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = getString(R.string.earbud_service_desc)
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification: Notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_clock_1794253815250)
            .setContentTitle("Earbud Hands-Free Listener Active")
            .setContentText("Tap earbud buttons to announce time or trigger actions")
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        startForeground(1001, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "com.example.ACTION_SPEAK_TIME_DIRECT") {
            val speaker = TimeSpeaker.getInstance(this)
            checkNextAlarmAndSpeak(this, speaker, prefsRepo.preferences.value.is24Hour)
        }
        return START_STICKY
    }

    override fun onDestroy() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest?.let { audioManager?.abandonAudioFocusRequest(it) }
            } else {
                @Suppress("DEPRECATION")
                audioManager?.abandonAudioFocus(null)
            }
        } catch (e: Exception) {
            // ignore
        }
        mediaSession?.release()
        mediaSession = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val TAG = "EarbudMediaService"
        private var lastClickTime = 0L
        private var clickCount = 0
        private var pendingClickJob: Job? = null

        fun handleMediaKeyEvent(context: Context, keyEvent: KeyEvent) {
            if (keyEvent.action != KeyEvent.ACTION_DOWN) return
            val prefs = UserPreferencesRepository(context).preferences.value

            when (keyEvent.keyCode) {
                KeyEvent.KEYCODE_HEADSETHOOK,
                KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                KeyEvent.KEYCODE_MEDIA_PLAY,
                KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                    handleMultiClickDirect(context)
                }
                KeyEvent.KEYCODE_MEDIA_NEXT,
                KeyEvent.KEYCODE_MEDIA_FAST_FORWARD -> {
                    executeAction(context, prefs.earbudDoublePress)
                }
                KeyEvent.KEYCODE_MEDIA_PREVIOUS,
                KeyEvent.KEYCODE_MEDIA_REWIND -> {
                    executeAction(context, prefs.earbudTriplePress)
                }
            }
        }

        fun handleMultiClickDirect(context: Context) {
            val now = SystemClock.elapsedRealtime()
            if (now - lastClickTime < 500) {
                clickCount++
            } else {
                clickCount = 1
            }
            lastClickTime = now

            val prefs = UserPreferencesRepository(context).preferences.value

            pendingClickJob?.cancel()
            pendingClickJob = CoroutineScope(Dispatchers.Main).launch {
                delay(400)
                when (clickCount) {
                    1 -> executeAction(context, prefs.earbudSinglePress)
                    2 -> executeAction(context, prefs.earbudDoublePress)
                    3 -> executeAction(context, prefs.earbudTriplePress)
                }
                clickCount = 0
            }
        }

        fun executeAction(context: Context, action: EarbudAction) {
            val speaker = TimeSpeaker.getInstance(context)
            val prefs = UserPreferencesRepository(context).preferences.value

            when (action) {
                EarbudAction.SPEAK_TIME -> {
                    checkNextAlarmAndSpeak(context, speaker, prefs.is24Hour)
                }
                EarbudAction.SPEAK_NEXT_ALARM -> {
                    val db = Room.databaseBuilder(context, AlarmDatabase::class.java, "auraclock_db")
                        .fallbackToDestructiveMigration()
                        .build()
                    CoroutineScope(Dispatchers.IO).launch {
                        val alarms = db.alarmDao().getEnabledAlarms()
                        if (alarms.isEmpty()) {
                            speaker.speakText("You have no active alarms set.")
                        } else {
                            val first = alarms.first()
                            val timeStr = first.formattedTime(prefs.is24Hour)
                            speaker.speakText("Next alarm is set for $timeStr. ${first.label}.")
                        }
                    }
                }
                EarbudAction.SPEAK_TIMER_STATUS -> {
                    speaker.speakText("Countdown timer status checked.")
                }
                EarbudAction.TOGGLE_STOPWATCH -> {
                    context.sendBroadcast(Intent("com.example.ACTION_TOGGLE_STOPWATCH"))
                    speaker.speakText("Stopwatch toggled.")
                }
                EarbudAction.LAP_STOPWATCH -> {
                    context.sendBroadcast(Intent("com.example.ACTION_LAP_STOPWATCH"))
                    speaker.speakText("Lap recorded.")
                }
                EarbudAction.TOGGLE_TIMER -> {
                    context.sendBroadcast(Intent("com.example.ACTION_TOGGLE_TIMER"))
                    speaker.speakText("Timer toggled.")
                }
                EarbudAction.NEXT_WATCH_FACE -> {
                    context.sendBroadcast(Intent("com.example.ACTION_NEXT_WATCHFACE"))
                    speaker.speakText("Watch face switched.")
                }
                EarbudAction.SNOOZE_ALARM -> {
                    AlarmSoundPlayer.stop()
                    speaker.speakText("Alarm dismissed.")
                }
            }
        }

        private fun checkNextAlarmAndSpeak(context: Context, speaker: TimeSpeaker, is24Hour: Boolean) {
            val db = Room.databaseBuilder(context, AlarmDatabase::class.java, "auraclock_db")
                .fallbackToDestructiveMigration()
                .build()
            CoroutineScope(Dispatchers.IO).launch {
                val alarms = db.alarmDao().getEnabledAlarms()
                val nextAlarmText = if (alarms.isNotEmpty()) {
                    val next = alarms.first()
                    "Next alarm is at ${next.formattedTime(is24Hour)}."
                } else null
                speaker.speakTime(is24Hour, nextAlarmText)
            }
        }

        fun startService(context: Context) {
            try {
                val intent = Intent(context, EarbudMediaService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed starting EarbudMediaService", e)
            }
        }

        fun stopService(context: Context) {
            try {
                context.stopService(Intent(context, EarbudMediaService::class.java))
            } catch (e: Exception) {
                Log.e(TAG, "Failed stopping EarbudMediaService", e)
            }
        }
    }
}
