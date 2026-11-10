package com.example.speech

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TimeSpeaker private constructor(context: Context) : TextToSpeech.OnInitListener {
    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private val pendingMessages = mutableListOf<String>()

    init {
        try {
            tts = TextToSpeech(appContext, this)
        } catch (e: Exception) {
            Log.e("TimeSpeaker", "Failed to initialize TTS", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                val result = engine.setLanguage(Locale.getDefault())
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    engine.setLanguage(Locale.US)
                }
                engine.setSpeechRate(0.95f)
                engine.setPitch(1.05f)
                isInitialized = true

                synchronized(pendingMessages) {
                    for (msg in pendingMessages) {
                        speakInternal(msg)
                    }
                    pendingMessages.clear()
                }
            }
        } else {
            Log.e("TimeSpeaker", "TTS init failed with status: $status")
        }
    }

    fun speakTime(is24Hour: Boolean, nextAlarmText: String? = null) {
        val now = Date()
        val timePattern = if (is24Hour) "HH:mm" else "h:mm a"
        val timeFormat = SimpleDateFormat(timePattern, Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())

        val timeStr = timeFormat.format(now)
        val dateStr = dateFormat.format(now)

        val fullAnnouncement = buildString {
            append("The time is $timeStr on $dateStr.")
            if (!nextAlarmText.isNullOrBlank()) {
                append(" $nextAlarmText")
            }
        }
        speakText(fullAnnouncement)
    }

    fun speakText(text: String) {
        if (!isInitialized) {
            synchronized(pendingMessages) {
                pendingMessages.add(text)
            }
        } else {
            speakInternal(text)
        }
    }

    private fun speakInternal(text: String) {
        val params = Bundle()
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, params, "AURA_VOICE_${System.currentTimeMillis()}")
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
            isInitialized = false
        } catch (e: Exception) {
            Log.e("TimeSpeaker", "Error shutting down TTS", e)
        }
    }

    companion object {
        @Volatile
        private var instance: TimeSpeaker? = null

        fun getInstance(context: Context): TimeSpeaker {
            return instance ?: synchronized(this) {
                instance ?: TimeSpeaker(context).also { instance = it }
            }
        }
    }
}
