package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val OledBlack = Color(0xFF000000)
val DarkSurface = Color(0xFF0A0E17)
val DarkSurfaceVariant = Color(0xFF131926)
val BorderSubtle = Color(0xFF1F293D)
val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextTertiary = Color(0xFF64748B)

val CyberCyan = Color(0xFF00F5FF)
val AmberGlow = Color(0xFFFF9E0B)
val CrimsonRed = Color(0xFFFF2A48)

private val OledColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF003840),
    onPrimaryContainer = CyberCyan,
    secondary = AmberGlow,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF3E2300),
    onSecondaryContainer = AmberGlow,
    tertiary = CrimsonRed,
    background = OledBlack,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle
)

@Composable
fun AuraClockTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = OledColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    AuraClockTheme(content = content)
}
