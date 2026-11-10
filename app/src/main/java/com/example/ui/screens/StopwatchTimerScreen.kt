package com.example.ui.screens

import android.app.Activity
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.service.AlarmSoundPlayer
import com.example.ui.components.AnalogStopwatchDial
import com.example.ui.components.AnalogTimerDial
import com.example.ui.viewmodel.ClockViewModel

@Composable
fun StopwatchTimerScreen(
    viewModel: ClockViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs by viewModel.preferences.collectAsState()
    val theme = prefs.effectiveTheme()
    val stopwatchElapsed by viewModel.stopwatchElapsed.collectAsState()
    val isStopwatchRunning by viewModel.isStopwatchRunning.collectAsState()
    val laps by viewModel.laps.collectAsState()

    val timerTotalSeconds by viewModel.timerTotalSeconds.collectAsState()
    val timerRemainingSeconds by viewModel.timerRemainingSeconds.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()
    val isTimerRinging by viewModel.isTimerRinging.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Analog Stopwatch", "Countdown Timer")

    var isPreviewingTone by remember { mutableStateOf(false) }

    // System / Pixel sound picker launcher for countdown timer
    val timerSoundPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI, Uri::class.java)
            } else {
                @Suppress("DEPRECATION")
                result.data?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            }
            if (uri != null) {
                val ringtone = RingtoneManager.getRingtone(context, uri)
                val title = ringtone?.getTitle(context) ?: "Pixel Sound"
                viewModel.setTimerSound(title, uri.toString())
            } else {
                viewModel.setTimerSound("Default Alarm", "")
            }
        }
    }

    // Flashing animation for active timer alarm
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(500),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
    ) {
        // Tab Row
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF0B101D),
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = theme.primaryColor
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTab == index) theme.primaryColor else Color(0xFF94A3B8),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.testTag("tab_$index")
                )
            }
        }

        // Active Timer Ringing Alert Banner (Dismiss Option)
        AnimatedVisibility(visible = isTimerRinging) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("timer_ringing_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF7F1D1D).copy(alpha = pulseAlpha))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Timer Complete!",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Tone playing: ${prefs.timerSoundTitle}",
                                color = Color(0xFFFECACA),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Button(
                        onClick = { viewModel.dismissTimerSound() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF991B1B)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("dismiss_timer_sound_button")
                    ) {
                        Text("STOP TONE", fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // Timer Sound Selector Bar (when Countdown Timer tab is selected)
        if (selectedTab == 1) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clickable {
                        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
                            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM)
                            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Timer Tone")
                            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true)
                            putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false)
                            if (prefs.timerSoundUri.isNotBlank()) {
                                putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse(prefs.timerSoundUri))
                            } else {
                                putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
                            }
                        }
                        timerSoundPickerLauncher.launch(intent)
                    }
                    .testTag("timer_sound_picker_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = theme.primaryColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Timer Sound: ${prefs.timerSoundTitle}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Tap to pick Pixel / device sound",
                                color = Color(0xFF64748B),
                                fontSize = 10.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = {
                            if (isPreviewingTone) {
                                AlarmSoundPlayer.stop()
                                isPreviewingTone = false
                            } else {
                                AlarmSoundPlayer.playSound(
                                    context = context,
                                    soundUriString = prefs.timerSoundUri,
                                    fallbackTone = prefs.timerSoundTitle,
                                    vibrate = false,
                                    autoStopSeconds = 10L
                                )
                                isPreviewingTone = true
                            }
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (isPreviewingTone) Icons.Default.Stop else Icons.Default.PlayArrow,
                            contentDescription = "Preview Timer Tone",
                            tint = theme.primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            when (selectedTab) {
                0 -> {
                    AnalogStopwatchDial(
                        elapsedMillis = stopwatchElapsed,
                        isRunning = isStopwatchRunning,
                        laps = laps,
                        theme = theme,
                        onStart = { viewModel.startStopwatch() },
                        onPause = { viewModel.pauseStopwatch() },
                        onReset = { viewModel.resetStopwatch() },
                        onLap = { viewModel.recordLap() }
                    )
                }
                1 -> {
                    AnalogTimerDial(
                        totalDurationSeconds = timerTotalSeconds,
                        remainingSeconds = timerRemainingSeconds,
                        isRunning = isTimerRunning,
                        theme = theme,
                        onStart = { viewModel.startTimer() },
                        onPause = { viewModel.pauseTimer() },
                        onReset = { viewModel.resetTimer() },
                        onAddSeconds = { viewModel.addTimerSeconds(it) },
                        onSetPreset = { viewModel.setTimerPreset(it) }
                    )
                }
            }
        }
    }
}
