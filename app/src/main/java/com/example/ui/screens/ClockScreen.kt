package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BurnInShieldBox
import com.example.ui.components.WatchFaceRenderer
import com.example.ui.viewmodel.ClockViewModel

@Composable
fun ClockScreen(
    viewModel: ClockViewModel,
    onLaunchNightstand: () -> Unit
) {
    var selectedNavIndex by remember { mutableIntStateOf(0) }
    val prefs by viewModel.preferences.collectAsState()
    val theme = prefs.effectiveTheme()
    val calendar by viewModel.currentTime.collectAsState()
    val alarms by viewModel.alarms.collectAsState()
    val batteryLevel by viewModel.batteryLevel.collectAsState()

    val nextAlarm = alarms.firstOrNull { it.isEnabled }
    val nextAlarmText = nextAlarm?.let { "Next: ${it.formattedTime(prefs.is24Hour)}" }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("clock_screen_scaffold"),
        containerColor = Color(0xFF000000),
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF080C14),
                tonalElevation = 0.dp
            ) {
                val navItems = listOf(
                    Triple("Clock", Icons.Default.Schedule, 0),
                    Triple("Alarms", Icons.Default.Alarm, 1),
                    Triple("Stopwatch", Icons.Default.Timer, 2),
                    Triple("Earbuds", Icons.Default.Headphones, 3),
                    Triple("Customize", Icons.Default.Palette, 4)
                )

                navItems.forEach { (label, icon, index) ->
                    NavigationBarItem(
                        selected = selectedNavIndex == index,
                        onClick = { selectedNavIndex = index },
                        icon = {
                            Icon(icon, contentDescription = label, modifier = Modifier.size(22.dp))
                        },
                        label = {
                            Text(label, fontSize = 11.sp, fontWeight = if (selectedNavIndex == index) FontWeight.Bold else FontWeight.Normal)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = theme.primaryColor,
                            indicatorColor = theme.primaryColor,
                            unselectedIconColor = Color(0xFF64748B),
                            unselectedTextColor = Color(0xFF64748B)
                        ),
                        modifier = Modifier.testTag("nav_item_$index")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (selectedNavIndex) {
                0 -> {
                    // Watch Face Screen
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        // Top Quick Action Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Cycle Face button
                            Button(
                                onClick = { viewModel.cycleNextWatchFace() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0F172A),
                                    contentColor = theme.primaryColor
                                ),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .height(38.dp)
                                    .testTag("cycle_face_button")
                            ) {
                                Text(
                                    text = prefs.selectedWatchFace.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("▾", fontSize = 12.sp)
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Speak time button
                                IconButton(
                                    onClick = { viewModel.speakCurrentTime() },
                                    modifier = Modifier
                                        .background(Color(0xFF0F172A), CircleShape)
                                        .size(38.dp)
                                        .testTag("speak_time_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VolumeUp,
                                        contentDescription = "Speak Time Aloud",
                                        tint = theme.accentColor,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Nightstand launcher button
                                Button(
                                    onClick = onLaunchNightstand,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF1E293B),
                                        contentColor = Color.White
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .height(38.dp)
                                        .testTag("launch_nightstand_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Nightlight,
                                        contentDescription = null,
                                        tint = Color(0xFFFF9E0B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Nightstand",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        // Main Watch Face Renderer
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            BurnInShieldBox(
                                modifier = Modifier.fillMaxSize(),
                                enabled = prefs.burnInShiftEnabled,
                                dimLevel = 1.0f,
                                glowColor = theme.glowColor
                            ) {
                                WatchFaceRenderer(
                                    watchFaceType = prefs.selectedWatchFace,
                                    theme = theme,
                                    calendar = calendar,
                                    is24Hour = prefs.is24Hour,
                                    showSeconds = prefs.showSeconds,
                                    nextAlarmText = nextAlarmText,
                                    batteryPercent = batteryLevel
                                )
                            }
                        }
                    }
                }
                1 -> AlarmListScreen(viewModel = viewModel)
                2 -> StopwatchTimerScreen(viewModel = viewModel)
                3 -> EarbudSettingsScreen(viewModel = viewModel)
                4 -> CustomizationScreen(viewModel = viewModel)
            }
        }
    }
}
