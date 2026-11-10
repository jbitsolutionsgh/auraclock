package com.example.data

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.example.data.model.ClockTheme
import com.example.data.model.EarbudAction
import com.example.data.model.WatchFaceType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class UserPreferences(
    val selectedWatchFace: WatchFaceType = WatchFaceType.CALIFORNIA,
    val selectedTheme: ClockTheme = ClockTheme.CYBER_CYAN,
    val isCustomColor: Boolean = false,
    val customColorValue: Long = 0xFF00F5FF,
    val is24Hour: Boolean = false,
    val showSeconds: Boolean = true,
    val burnInShiftEnabled: Boolean = true,
    val nightstandDimLevel: Float = 0.65f,
    val keepScreenOn: Boolean = true,
    val earbudSinglePress: EarbudAction = EarbudAction.SPEAK_TIME,
    val earbudDoublePress: EarbudAction = EarbudAction.TOGGLE_STOPWATCH,
    val earbudTriplePress: EarbudAction = EarbudAction.SPEAK_NEXT_ALARM,
    val earbudServiceEnabled: Boolean = true,
    val timerSoundTitle: String = "Default Alarm",
    val timerSoundUri: String = "",
    val hasSeededInitialAlarms: Boolean = false
) {
    fun effectiveTheme(): ClockTheme {
        return if (isCustomColor) {
            ClockTheme.fromCustomColor(Color(customColorValue))
        } else {
            selectedTheme
        }
    }
}

class UserPreferencesRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auraclock_prefs", Context.MODE_PRIVATE)

    private val _preferences = MutableStateFlow(loadPreferences())
    val preferences: StateFlow<UserPreferences> = _preferences.asStateFlow()

    private fun loadPreferences(): UserPreferences {
        val faceName = prefs.getString("watch_face", WatchFaceType.CALIFORNIA.name) ?: WatchFaceType.CALIFORNIA.name
        val themeTitle = prefs.getString("clock_theme_title", ClockTheme.CYBER_CYAN.title) ?: ClockTheme.CYBER_CYAN.title
        val isCustom = prefs.getBoolean("is_custom_color", false)
        val customColorVal = prefs.getLong("custom_color_val", 0xFF00F5FF)

        val is24 = prefs.getBoolean("is_24_hour", false)
        val seconds = prefs.getBoolean("show_seconds", true)
        val burnIn = prefs.getBoolean("burn_in_shift", true)
        val dim = prefs.getFloat("nightstand_dim", 0.65f)
        val keepAwake = prefs.getBoolean("keep_screen_on", true)

        val singleAction = prefs.getString("earbud_single", EarbudAction.SPEAK_TIME.name) ?: EarbudAction.SPEAK_TIME.name
        val doubleAction = prefs.getString("earbud_double", EarbudAction.TOGGLE_STOPWATCH.name) ?: EarbudAction.TOGGLE_STOPWATCH.name
        val tripleAction = prefs.getString("earbud_triple", EarbudAction.SPEAK_NEXT_ALARM.name) ?: EarbudAction.SPEAK_NEXT_ALARM.name
        val earbudEnabled = prefs.getBoolean("earbud_service_enabled", true)

        val timerSound = prefs.getString("timer_sound_title", "Default Alarm") ?: "Default Alarm"
        val timerUri = prefs.getString("timer_sound_uri", "") ?: ""
        val seeded = prefs.getBoolean("has_seeded_initial_alarms", false)

        val theme = ClockTheme.PRESET_THEMES.firstOrNull { it.title == themeTitle } ?: ClockTheme.CYBER_CYAN

        return UserPreferences(
            selectedWatchFace = try { WatchFaceType.valueOf(faceName) } catch (e: Exception) { WatchFaceType.CALIFORNIA },
            selectedTheme = theme,
            isCustomColor = isCustom,
            customColorValue = customColorVal,
            is24Hour = is24,
            showSeconds = seconds,
            burnInShiftEnabled = burnIn,
            nightstandDimLevel = dim,
            keepScreenOn = keepAwake,
            earbudSinglePress = try { EarbudAction.valueOf(singleAction) } catch (e: Exception) { EarbudAction.SPEAK_TIME },
            earbudDoublePress = try { EarbudAction.valueOf(doubleAction) } catch (e: Exception) { EarbudAction.TOGGLE_STOPWATCH },
            earbudTriplePress = try { EarbudAction.valueOf(tripleAction) } catch (e: Exception) { EarbudAction.SPEAK_NEXT_ALARM },
            earbudServiceEnabled = earbudEnabled,
            timerSoundTitle = timerSound,
            timerSoundUri = timerUri,
            hasSeededInitialAlarms = seeded
        )
    }

    fun setWatchFace(face: WatchFaceType) {
        prefs.edit().putString("watch_face", face.name).apply()
        _preferences.value = _preferences.value.copy(selectedWatchFace = face)
    }

    fun setTheme(theme: ClockTheme) {
        prefs.edit()
            .putString("clock_theme_title", theme.title)
            .putBoolean("is_custom_color", false)
            .apply()
        _preferences.value = _preferences.value.copy(
            selectedTheme = theme,
            isCustomColor = false
        )
    }

    fun setCustomColor(color: Color) {
        val colorLong = color.toArgb().toLong() and 0xFFFFFFFFL
        prefs.edit()
            .putBoolean("is_custom_color", true)
            .putLong("custom_color_val", colorLong)
            .apply()
        _preferences.value = _preferences.value.copy(
            isCustomColor = true,
            customColorValue = colorLong
        )
    }

    fun set24Hour(enabled: Boolean) {
        prefs.edit().putBoolean("is_24_hour", enabled).apply()
        _preferences.value = _preferences.value.copy(is24Hour = enabled)
    }

    fun setShowSeconds(enabled: Boolean) {
        prefs.edit().putBoolean("show_seconds", enabled).apply()
        _preferences.value = _preferences.value.copy(showSeconds = enabled)
    }

    fun setBurnInShift(enabled: Boolean) {
        prefs.edit().putBoolean("burn_in_shift", enabled).apply()
        _preferences.value = _preferences.value.copy(burnInShiftEnabled = enabled)
    }

    fun setNightstandDimLevel(dim: Float) {
        prefs.edit().putFloat("nightstand_dim", dim).apply()
        _preferences.value = _preferences.value.copy(nightstandDimLevel = dim)
    }

    fun setKeepScreenOn(enabled: Boolean) {
        prefs.edit().putBoolean("keep_screen_on", enabled).apply()
        _preferences.value = _preferences.value.copy(keepScreenOn = enabled)
    }

    fun setEarbudSinglePress(action: EarbudAction) {
        prefs.edit().putString("earbud_single", action.name).apply()
        _preferences.value = _preferences.value.copy(earbudSinglePress = action)
    }

    fun setEarbudDoublePress(action: EarbudAction) {
        prefs.edit().putString("earbud_double", action.name).apply()
        _preferences.value = _preferences.value.copy(earbudDoublePress = action)
    }

    fun setEarbudTriplePress(action: EarbudAction) {
        prefs.edit().putString("earbud_triple", action.name).apply()
        _preferences.value = _preferences.value.copy(earbudTriplePress = action)
    }

    fun setEarbudServiceEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("earbud_service_enabled", enabled).apply()
        _preferences.value = _preferences.value.copy(earbudServiceEnabled = enabled)
    }

    fun setTimerSound(title: String, uri: String) {
        prefs.edit()
            .putString("timer_sound_title", title)
            .putString("timer_sound_uri", uri)
            .apply()
        _preferences.value = _preferences.value.copy(
            timerSoundTitle = title,
            timerSoundUri = uri
        )
    }

    fun markAlarmsSeeded() {
        prefs.edit().putBoolean("has_seeded_initial_alarms", true).apply()
        _preferences.value = _preferences.value.copy(hasSeededInitialAlarms = true)
    }
}
