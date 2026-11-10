package com.example.ui.viewmodel

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.UserPreferences
import com.example.data.UserPreferencesRepository
import com.example.data.db.AlarmDatabase
import com.example.data.db.AlarmEntity
import com.example.data.model.ClockTheme
import com.example.data.model.EarbudAction
import com.example.data.model.WatchFaceType
import com.example.service.AlarmScheduler
import com.example.service.AlarmSoundPlayer
import com.example.service.EarbudMediaService
import com.example.speech.TimeSpeaker
import com.example.ui.components.LapRecord
import com.example.widget.AuraClockWidgetProvider
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Calendar

class ClockViewModel(application: Application) : AndroidViewModel(application) {
    private val context = application.applicationContext
    private val prefsRepo = UserPreferencesRepository(context)
    private val db = Room.databaseBuilder(context, AlarmDatabase::class.java, "auraclock_db")
        .fallbackToDestructiveMigration()
        .build()

    val preferences: StateFlow<UserPreferences> = prefsRepo.preferences

    val alarms: StateFlow<List<AlarmEntity>> = db.alarmDao().getAllAlarmsFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live Calendar time tick
    private val _currentTime = MutableStateFlow(Calendar.getInstance())
    val currentTime: StateFlow<Calendar> = _currentTime.asStateFlow()

    // Stopwatch State
    private val _stopwatchElapsed = MutableStateFlow(0L)
    val stopwatchElapsed: StateFlow<Long> = _stopwatchElapsed.asStateFlow()

    private val _isStopwatchRunning = MutableStateFlow(false)
    val isStopwatchRunning: StateFlow<Boolean> = _isStopwatchRunning.asStateFlow()

    private val _laps = MutableStateFlow<List<LapRecord>>(emptyList())
    val laps: StateFlow<List<LapRecord>> = _laps.asStateFlow()

    private var stopwatchJob: Job? = null
    private var stopwatchBaseTime = 0L

    // Timer State
    private val _timerTotalSeconds = MutableStateFlow(300L) // 5 min default
    val timerTotalSeconds: StateFlow<Long> = _timerTotalSeconds.asStateFlow()

    private val _timerRemainingSeconds = MutableStateFlow(300L)
    val timerRemainingSeconds: StateFlow<Long> = _timerRemainingSeconds.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _isTimerRinging = MutableStateFlow(false)
    val isTimerRinging: StateFlow<Boolean> = _isTimerRinging.asStateFlow()

    private var timerJob: Job? = null

    // Battery percentage simulation/fallback
    private val _batteryLevel = MutableStateFlow(92)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow()

    // Broadcast receiver for inter-service communication (e.g. earbud events)
    private val actionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                "com.example.ACTION_TOGGLE_STOPWATCH" -> toggleStopwatch()
                "com.example.ACTION_LAP_STOPWATCH" -> recordLap()
                "com.example.ACTION_TOGGLE_TIMER" -> toggleTimer()
                "com.example.ACTION_NEXT_WATCHFACE" -> cycleNextWatchFace()
            }
        }
    }

    init {
        // Start high-precision 1-second clock ticker (or 50ms for smooth sub-seconds)
        viewModelScope.launch {
            while (isActive) {
                _currentTime.value = Calendar.getInstance()
                delay(if (preferences.value.showSeconds) 1000L else 2000L)
            }
        }

        // Initialize default sample alarm only once on first install
        viewModelScope.launch {
            if (!prefsRepo.preferences.value.hasSeededInitialAlarms) {
                val existing = db.alarmDao().getAllAlarms()
                if (existing.isEmpty()) {
                    val defaultAlarm = AlarmEntity(
                        hour = 7,
                        minute = 0,
                        daysOfWeekMask = 62, // Weekdays
                        isEnabled = true,
                        label = "Morning Wake Up",
                        soundTone = "System Default",
                        soundUri = ""
                    )
                    val id = db.alarmDao().insertAlarm(defaultAlarm)
                    AlarmScheduler.scheduleAlarm(context, defaultAlarm.copy(id = id))
                    AuraClockWidgetProvider.updateAllWidgets(context)
                }
                prefsRepo.markAlarmsSeeded()
            }
        }

        // Register internal action receiver
        val filter = IntentFilter().apply {
            addAction("com.example.ACTION_TOGGLE_STOPWATCH")
            addAction("com.example.ACTION_LAP_STOPWATCH")
            addAction("com.example.ACTION_TOGGLE_TIMER")
            addAction("com.example.ACTION_NEXT_WATCHFACE")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(actionReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(actionReceiver, filter)
        }

        // Start earbud service if enabled
        if (preferences.value.earbudServiceEnabled) {
            EarbudMediaService.startService(context)
        }
    }

    // --- Preferences Actions ---
    fun setWatchFace(face: WatchFaceType) {
        prefsRepo.setWatchFace(face)
    }

    fun setTheme(theme: ClockTheme) {
        prefsRepo.setTheme(theme)
    }

    fun setCustomColor(color: androidx.compose.ui.graphics.Color) {
        prefsRepo.setCustomColor(color)
    }

    fun set24Hour(is24: Boolean) {
        prefsRepo.set24Hour(is24)
        AuraClockWidgetProvider.updateAllWidgets(context)
    }

    fun setShowSeconds(show: Boolean) {
        prefsRepo.setShowSeconds(show)
    }

    fun setBurnInShift(enabled: Boolean) {
        prefsRepo.setBurnInShift(enabled)
    }

    fun setNightstandDimLevel(dim: Float) {
        prefsRepo.setNightstandDimLevel(dim)
    }

    fun setKeepScreenOn(enabled: Boolean) {
        prefsRepo.setKeepScreenOn(enabled)
    }

    fun setEarbudAction(pressType: Int, action: EarbudAction) {
        when (pressType) {
            1 -> prefsRepo.setEarbudSinglePress(action)
            2 -> prefsRepo.setEarbudDoublePress(action)
            3 -> prefsRepo.setEarbudTriplePress(action)
        }
    }

    fun setEarbudServiceEnabled(enabled: Boolean) {
        prefsRepo.setEarbudServiceEnabled(enabled)
        if (enabled) {
            EarbudMediaService.startService(context)
        } else {
            EarbudMediaService.stopService(context)
        }
    }

    fun cycleNextWatchFace() {
        val faces = WatchFaceType.values()
        val currentIndex = faces.indexOf(preferences.value.selectedWatchFace)
        val nextFace = faces[(currentIndex + 1) % faces.size]
        setWatchFace(nextFace)
    }

    // --- Alarm Actions ---
    fun addOrUpdateAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            if (alarm.id == 0L) {
                val newId = db.alarmDao().insertAlarm(alarm)
                AlarmScheduler.scheduleAlarm(context, alarm.copy(id = newId))
            } else {
                db.alarmDao().updateAlarm(alarm)
                if (alarm.isEnabled) {
                    AlarmScheduler.scheduleAlarm(context, alarm)
                } else {
                    AlarmScheduler.cancelAlarm(context, alarm.id)
                }
            }
            AuraClockWidgetProvider.updateAllWidgets(context)
        }
    }

    fun toggleAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            val updated = alarm.copy(isEnabled = !alarm.isEnabled)
            db.alarmDao().updateAlarm(updated)
            if (updated.isEnabled) {
                AlarmScheduler.scheduleAlarm(context, updated)
            } else {
                AlarmScheduler.cancelAlarm(context, alarm.id)
            }
            AuraClockWidgetProvider.updateAllWidgets(context)
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            AlarmScheduler.cancelAlarm(context, alarm.id)
            db.alarmDao().deleteAlarm(alarm)
            AuraClockWidgetProvider.updateAllWidgets(context)
        }
    }

    fun speakCurrentTime() {
        val next = alarms.value.firstOrNull { it.isEnabled }
        val alarmNote = if (next != null) {
            "Next alarm is at ${next.formattedTime(preferences.value.is24Hour)}."
        } else null
        TimeSpeaker.getInstance(context).speakTime(preferences.value.is24Hour, alarmNote)
    }

    // --- Stopwatch Actions ---
    fun startStopwatch() {
        if (_isStopwatchRunning.value) return
        _isStopwatchRunning.value = true
        stopwatchBaseTime = SystemClock.elapsedRealtime() - _stopwatchElapsed.value

        stopwatchJob = viewModelScope.launch {
            while (isActive && _isStopwatchRunning.value) {
                _stopwatchElapsed.value = SystemClock.elapsedRealtime() - stopwatchBaseTime
                delay(30) // ~30 fps smooth centisecond movement
            }
        }
    }

    fun pauseStopwatch() {
        _isStopwatchRunning.value = false
        stopwatchJob?.cancel()
        stopwatchJob = null
    }

    fun resetStopwatch() {
        pauseStopwatch()
        _stopwatchElapsed.value = 0L
        _laps.value = emptyList()
    }

    fun recordLap() {
        val total = _stopwatchElapsed.value
        val currentLaps = _laps.value
        val lastTotal = if (currentLaps.isNotEmpty()) currentLaps.last().totalTimeMillis else 0L
        val lapTime = total - lastTotal

        val newRecord = LapRecord(
            lapNumber = currentLaps.size + 1,
            lapTimeMillis = lapTime,
            totalTimeMillis = total
        )
        _laps.value = currentLaps + newRecord
    }

    fun toggleStopwatch() {
        if (_isStopwatchRunning.value) pauseStopwatch() else startStopwatch()
    }

    // --- Countdown Timer Actions ---
    fun setTimerPreset(seconds: Long) {
        pauseTimer()
        _timerTotalSeconds.value = seconds
        _timerRemainingSeconds.value = seconds
    }

    fun addTimerSeconds(seconds: Long) {
        val newSec = (_timerRemainingSeconds.value + seconds).coerceAtLeast(10L)
        _timerRemainingSeconds.value = newSec
        _timerTotalSeconds.value = _timerTotalSeconds.value.coerceAtLeast(newSec)
    }

    fun startTimer() {
        if (_isTimerRunning.value || _timerRemainingSeconds.value <= 0) return
        _isTimerRunning.value = true
        dismissTimerSound()

        timerJob = viewModelScope.launch {
            while (isActive && _timerRemainingSeconds.value > 0) {
                delay(1000L)
                if (_timerRemainingSeconds.value > 0) {
                    _timerRemainingSeconds.value = _timerRemainingSeconds.value - 1
                }
            }
            if (_timerRemainingSeconds.value <= 0) {
                _isTimerRunning.value = false
                _isTimerRinging.value = true
                val timerUri = preferences.value.timerSoundUri
                val timerTitle = preferences.value.timerSoundTitle
                AlarmSoundPlayer.playSound(
                    context = context,
                    soundUriString = timerUri,
                    fallbackTone = timerTitle,
                    vibrate = true,
                    autoStopSeconds = 60L
                )
                TimeSpeaker.getInstance(context).speakText("Timer complete!")
            }
        }
    }

    fun dismissTimerSound() {
        _isTimerRinging.value = false
        AlarmSoundPlayer.stop()
    }

    fun setTimerSound(title: String, uri: String) {
        prefsRepo.setTimerSound(title, uri)
    }

    fun pauseTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        timerJob = null
        dismissTimerSound()
    }

    fun resetTimer() {
        pauseTimer()
        dismissTimerSound()
        _timerRemainingSeconds.value = _timerTotalSeconds.value
    }

    fun toggleTimer() {
        if (_isTimerRunning.value) pauseTimer() else startTimer()
    }

    override fun onCleared() {
        try {
            context.unregisterReceiver(actionReceiver)
        } catch (e: Exception) {
            // ignore
        }
        super.onCleared()
    }
}
