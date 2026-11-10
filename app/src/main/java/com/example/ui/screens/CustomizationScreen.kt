package com.example.ui.screens

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClockTheme
import com.example.data.model.WatchFaceType
import com.example.ui.components.CustomGradientPicker
import com.example.ui.viewmodel.ClockViewModel

@Composable
fun CustomizationScreen(
    viewModel: ClockViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val prefs by viewModel.preferences.collectAsState()
    val effectiveTheme = prefs.effectiveTheme()

    var isPaletteExpanded by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF000000))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Header (no descriptions)
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = effectiveTheme.primaryColor,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Aesthetics & Faces",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }
        }

        // 8 Stylish Analogue Watch Faces Carousel (no descriptions)
        item {
            Text(
                text = "ANALOGUE WATCH FACES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = effectiveTheme.accentColor,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(WatchFaceType.values()) { face ->
                    val isSelected = face == prefs.selectedWatchFace
                    Card(
                        modifier = Modifier
                            .width(148.dp)
                            .height(148.dp)
                            .clickable { viewModel.setWatchFace(face) }
                            .testTag("face_card_${face.name}"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF0E1A30) else Color(0xFF0A0F1A)
                        ),
                        border = if (isSelected) {
                            androidx.compose.foundation.BorderStroke(2.dp, effectiveTheme.primaryColor)
                        } else null
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "ANALOGUE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) effectiveTheme.primaryColor else Color(0xFF64748B),
                                    letterSpacing = 1.sp
                                )

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = effectiveTheme.primaryColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            // Minimal Dial Silhouette preview with hands
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .border(
                                        1.5.dp,
                                        if (isSelected) effectiveTheme.primaryColor else Color(0xFF334155),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(3.dp)
                                        .background(if (isSelected) effectiveTheme.primaryColor else Color.White, CircleShape)
                                )
                            }

                            Text(
                                text = face.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // Collapsible Color Palette Chooser & Gradient Picker
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF080C16)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF162032))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Collapsible Header Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isPaletteExpanded = !isPaletteExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .background(effectiveTheme.primaryColor, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "COLOR PALETTE & GRADIENT",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = effectiveTheme.accentColor,
                                letterSpacing = 1.sp
                            )
                        }

                        IconButton(
                            onClick = { isPaletteExpanded = !isPaletteExpanded },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isPaletteExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = if (isPaletteExpanded) "Collapse" else "Expand",
                                tint = Color.White
                            )
                        }
                    }

                    // Collapsible Content
                    AnimatedVisibility(
                        visible = isPaletteExpanded,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        Column(
                            modifier = Modifier.padding(top = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            // 1. Preset Themes
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                ClockTheme.PRESET_THEMES.forEach { theme ->
                                    val isSelected = !prefs.isCustomColor && theme.title == prefs.selectedTheme.title
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.setTheme(theme) }
                                            .testTag("theme_card_${theme.title}"),
                                        shape = RoundedCornerShape(14.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) Color(0xFF111827) else Color(0xFF0A0F1A)
                                        ),
                                        border = if (isSelected) {
                                            androidx.compose.foundation.BorderStroke(1.5.dp, theme.primaryColor)
                                        } else null
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(horizontal = 14.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(26.dp)
                                                        .background(
                                                            Brush.linearGradient(listOf(theme.primaryColor, theme.secondaryColor)),
                                                            CircleShape
                                                        )
                                                )
                                                Spacer(modifier = Modifier.width(12.dp))
                                                Text(
                                                    text = theme.title,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White
                                                )
                                            }

                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = null,
                                                    tint = theme.primaryColor,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            // 2. Custom Gradient Picker
                            Text(
                                text = "CUSTOM GRADIENT SPECTRUM",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = effectiveTheme.accentColor,
                                letterSpacing = 1.sp
                            )

                            CustomGradientPicker(
                                initialColor = effectiveTheme.primaryColor,
                                onColorSelected = { pickedColor ->
                                    viewModel.setCustomColor(pickedColor)
                                }
                            )
                        }
                    }
                }
            }
        }

        // Display & Longevity Settings (no descriptions)
        item {
            Text(
                text = "DISPLAY PREFERENCES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = effectiveTheme.accentColor,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0B101D))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Show Seconds
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Show Seconds Hand", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = prefs.showSeconds,
                            onCheckedChange = { viewModel.setShowSeconds(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = effectiveTheme.primaryColor
                            )
                        )
                    }

                    // Burn-In Protection
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("OLED Burn-in Pixel Shifting", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = prefs.burnInShiftEnabled,
                            onCheckedChange = { viewModel.setBurnInShift(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = effectiveTheme.primaryColor
                            )
                        )
                    }

                    // Keep Screen On
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Keep Screen On in Nightstand", color = Color.White, fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = prefs.keepScreenOn,
                            onCheckedChange = { viewModel.setKeepScreenOn(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = effectiveTheme.primaryColor
                            )
                        )
                    }
                }
            }
        }

        // Screen Saver Setup Button
        item {
            Button(
                onClick = {
                    try {
                        context.startActivity(Intent(Settings.ACTION_DREAM_SETTINGS))
                    } catch (e: Exception) {
                        try {
                            context.startActivity(Intent(Settings.ACTION_DISPLAY_SETTINGS))
                        } catch (e2: Exception) {
                            // ignore
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1E293B),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("open_dream_settings_button")
            ) {
                Icon(Icons.Default.Launch, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Device Screen Saver Settings", fontWeight = FontWeight.Bold)
            }
        }

        item {
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}
