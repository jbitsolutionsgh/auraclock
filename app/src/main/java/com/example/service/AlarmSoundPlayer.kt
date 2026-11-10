package com.example.service

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

object AlarmSoundPlayer {
    val AVAILABLE_TONES = listOf(
        "System Default",
        "Pixel Horizon",
        "Zen Chimes",
        "Digital Pulse",
        "Morning Aura"
    )

    private var mediaPlayer: MediaPlayer? = null
    private var playbackJob: Job? = null
    private var autoStopJob: Job? = null
    private var vibrator: Vibrator? = null

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    fun playTone(context: Context, tone: String, vibrate: Boolean = false) {
        playSound(context, null, tone, vibrate, autoStopSeconds = 15L)
    }

    fun playSound(
        context: Context,
        soundUriString: String?,
        fallbackTone: String = "System Default",
        vibrate: Boolean = true,
        autoStopSeconds: Long = 60L
    ) {
        stop()

        if (vibrate) {
            startVibration(context)
        }

        _isPlaying.value = true

        // Schedule auto-stop so sound never rings forever if unattended
        autoStopJob = CoroutineScope(Dispatchers.Main).launch {
            delay(autoStopSeconds * 1000L)
            stop()
        }

        // Try playing user-picked sound URI (e.g. from Pixel sound picker)
        if (!soundUriString.isNullOrBlank()) {
            try {
                val soundUri = Uri.parse(soundUriString)
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(context, soundUri)
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    isLooping = true
                    prepare()
                    start()
                }
                return
            } catch (e: Exception) {
                Log.e("AlarmSoundPlayer", "Failed to play custom URI: $soundUriString", e)
            }
        }

        // Fallback to system default alarm or synthetic tone
        playSystemAlarmOrTone(context, fallbackTone)
    }

    private fun playSystemAlarmOrTone(context: Context, toneName: String) {
        try {
            val alert: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            mediaPlayer = MediaPlayer().apply {
                setDataSource(context, alert)
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                isLooping = true
                prepare()
                start()
            }
        } catch (e: Exception) {
            Log.e("AlarmSoundPlayer", "Failed to play system alarm, using synth fallback", e)
            playbackJob = CoroutineScope(Dispatchers.Default).launch {
                while (isActive) {
                    playChimeMelody()
                    delay(1200)
                }
            }
        }
    }

    private fun startVibration(context: Context) {
        try {
            vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            val pattern = longArrayOf(0, 400, 200, 400, 600)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, 0)
            }
        } catch (e: Exception) {
            Log.e("AlarmSoundPlayer", "Failed to start vibration", e)
        }
    }

    private suspend fun playChimeMelody() {
        playPureSineWave(528.0, 400, 0.6f)
        delay(150)
        playPureSineWave(660.0, 400, 0.7f)
        delay(150)
        playPureSineWave(792.0, 500, 0.8f)
        delay(200)
        playPureSineWave(1056.0, 800, 0.9f)
    }

    private fun playPureSineWave(frequency: Double, durationMs: Int, volume: Float) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000.0)).toInt()
        val generatedSnd = ByteArray(2 * numSamples)
        val decaySamples = (numSamples * 0.4).toInt()

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val angle = 2.0 * Math.PI * frequency * t
            val envelope = when {
                i < 200 -> i / 200.0
                i > numSamples - decaySamples -> (numSamples - i).toDouble() / decaySamples
                else -> 1.0
            }
            val sampleVal = (sin(angle) * 32767.0 * volume * envelope).toInt().coerceIn(-32768, 32767).toShort()
            generatedSnd[2 * i] = (sampleVal.toInt() and 0x00ff).toByte()
            generatedSnd[2 * i + 1] = ((sampleVal.toInt() and 0xff00) ushr 8).toByte()
        }

        try {
            val audioTrack = android.media.AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    android.media.AudioFormat.Builder()
                        .setEncoding(android.media.AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(android.media.AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(generatedSnd.size)
                .setTransferMode(android.media.AudioTrack.MODE_STATIC)
                .build()

            audioTrack.write(generatedSnd, 0, generatedSnd.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong())
            audioTrack.release()
        } catch (e: Exception) {
            Log.e("AlarmSoundPlayer", "Error outputting audio", e)
        }
    }

    fun stop() {
        _isPlaying.value = false
        autoStopJob?.cancel()
        autoStopJob = null
        playbackJob?.cancel()
        playbackJob = null

        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        } catch (e: Exception) {
            // ignore
        }
        try {
            vibrator?.cancel()
            vibrator = null
        } catch (e: Exception) {
            // ignore
        }
    }
}
