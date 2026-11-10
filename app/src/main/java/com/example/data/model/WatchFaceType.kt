package com.example.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

enum class WatchFaceType(
    val title: String,
    val subtitle: String = "Analogue Edition"
) {
    CYBER_CARVED_3D("Cyber 3D Glow", "Embossed Neon Dial"),
    MOTORSPORT_M("Motorsport M", "M-Performance Tachymeter"),
    SPEEDMASTER_RACING("Speedmaster Racing", "Honeycomb Co-Axial"),
    RACING_YELLOW_TACHY("Tachymeter Pro Yellow", "Split Cockpit Meter"),
    CASIO_ROYALE("Casio AE-1200 Royale", "Retro World Time LCD"),
    HAWKER_AVIATOR("Hawker Cockpit", "Turbine Flight Chrono"),
    MECHA_TACTICAL("Mecha Tactical", "Armor Plated Stencil"),
    SEIKO_DIGI_ANA("Vintage Digi-Ana", "Dual Display Dashboard"),
    CLUB_RETROGRADE("Club Night", "Retrograde Twin Barrel"),
    ROTARY_GAUGE("Rotary Sports Meter", "Mechanical Drum Gauge"),
    BMW_M_WHEEL("BMW M Wheel", "World Time Alloy Spokes"),
    ORANGE_TACTICAL_DIVER("Tactical Orange Diver", "Carbon Fiber Chrono"),
    COCKPIT_YELLOW("Cockpit 24H Yellow", "High Contrast Flieger"),
    TITANIUM_RED_CHRONO("Titanium Red Chrono", "Industrial Machined Bezel"),
    MODERN_PODS_SPORTS("Modern Pods Sports", "Multi-Telemetry Gauges"),

    CALIFORNIA("California", "Roman-Arabic Classic"),
    CHRONOGRAPH_PRO("Chronograph Pro", "Tri-Compax Precision"),
    AVIATOR_B("Aviator Type-B", "Flieger Cockpit Dial"),
    NAUTILUS_DIVER("Nautilus Diver", "Ocean Lume Bezel"),
    BAUHAUS_CLASSIC("Bauhaus Classic", "German Minimalist"),
    REGULATEUR("Regulator Master", "Dual Sub-dial Precision"),
    TOURBILLON_ROYAL("Tourbillon Royal", "Exposed Escapement"),
    FIELD_TACTICAL_24H("Field Tactical 24H", "Military 24-Hour Dial")
}

data class ClockTheme(
    val title: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val accentColor: Color,
    val glowColor: Color,
    val hexCode: String
) {
    companion object {
        val CYBER_CYAN = ClockTheme(
            title = "Cyber Cyan",
            primaryColor = Color(0xFF00F5FF),
            secondaryColor = Color(0xFF00B4D8),
            accentColor = Color(0xFF38BDF8),
            glowColor = Color(0x3300F5FF),
            hexCode = "#00F5FF"
        )
        val AMBER_NIGHT = ClockTheme(
            title = "Amber Night",
            primaryColor = Color(0xFFFF9E0B),
            secondaryColor = Color(0xFFFF6D00),
            accentColor = Color(0xFFFFB74D),
            glowColor = Color(0x33FF9E0B),
            hexCode = "#FF9E0B"
        )
        val SOLAR_CRIMSON = ClockTheme(
            title = "Solar Crimson",
            primaryColor = Color(0xFFFF2A48),
            secondaryColor = Color(0xFFDC2626),
            accentColor = Color(0xFFF87171),
            glowColor = Color(0x33FF2A48),
            hexCode = "#FF2A48"
        )
        val OLED_WHITE = ClockTheme(
            title = "OLED Pure White",
            primaryColor = Color(0xFFFFFFFF),
            secondaryColor = Color(0xFF94A3B8),
            accentColor = Color(0xFFE2E8F0),
            glowColor = Color(0x22FFFFFF),
            hexCode = "#FFFFFF"
        )
        val SAPPHIRE_DUSK = ClockTheme(
            title = "Sapphire Dusk",
            primaryColor = Color(0xFF818CF8),
            secondaryColor = Color(0xFF6366F1),
            accentColor = Color(0xFFA5B4FC),
            glowColor = Color(0x33818CF8),
            hexCode = "#818CF8"
        )
        val EMERALD_AURORA = ClockTheme(
            title = "Emerald Aurora",
            primaryColor = Color(0xFF10B981),
            secondaryColor = Color(0xFF059669),
            accentColor = Color(0xFF34D399),
            glowColor = Color(0x3310B981),
            hexCode = "#10B981"
        )
        val SUNSET_CORAL = ClockTheme(
            title = "Sunset Coral",
            primaryColor = Color(0xFFFB7185),
            secondaryColor = Color(0xFFF59E0B),
            accentColor = Color(0xFFFDA4AF),
            glowColor = Color(0x33FB7185),
            hexCode = "#FB7185"
        )

        val PRESET_THEMES = listOf(
            CYBER_CYAN,
            AMBER_NIGHT,
            SOLAR_CRIMSON,
            OLED_WHITE,
            SAPPHIRE_DUSK,
            EMERALD_AURORA,
            SUNSET_CORAL
        )

        fun fromCustomColor(color: Color): ClockTheme {
            val hex = String.format("#%06X", (0xFFFFFF and color.toArgb()))
            return ClockTheme(
                title = "Custom ($hex)",
                primaryColor = color,
                secondaryColor = color.copy(alpha = 0.85f),
                accentColor = color,
                glowColor = color.copy(alpha = 0.3f),
                hexCode = hex
            )
        }
    }
}

enum class EarbudAction(val label: String) {
    SPEAK_TIME("Read Current Time Aloud"),
    SPEAK_NEXT_ALARM("Read Next Alarm Status"),
    SPEAK_TIMER_STATUS("Read Timer / Stopwatch"),
    TOGGLE_STOPWATCH("Start / Pause Stopwatch"),
    LAP_STOPWATCH("Record Stopwatch Lap"),
    TOGGLE_TIMER("Start / Pause Timer"),
    NEXT_WATCH_FACE("Cycle Next Watch Face"),
    SNOOZE_ALARM("Snooze / Dismiss Alarm")
}
