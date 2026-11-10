package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.data.model.ClockTheme
import com.example.data.model.WatchFaceType
import java.util.Calendar
import kotlin.math.PI
import com.example.ui.components.faces.CyberCarved3DFace
import com.example.ui.components.faces.MotorsportMFace
import com.example.ui.components.faces.SpeedmasterRacingFace
import com.example.ui.components.faces.RacingYellowTachyFace
import com.example.ui.components.faces.CasioRoyaleFace
import com.example.ui.components.faces.HawkerAviatorFace
import com.example.ui.components.faces.MechaTacticalFace
import com.example.ui.components.faces.SeikoDigiAnaFace
import com.example.ui.components.faces.ClubRetrogradeFace
import com.example.ui.components.faces.RotaryGaugeFace
import com.example.ui.components.faces.BmwMWheelFace
import com.example.ui.components.faces.OrangeTacticalDiverFace
import com.example.ui.components.faces.CockpitYellowFace
import com.example.ui.components.faces.TitaniumRedChronoFace
import com.example.ui.components.faces.ModernPodsSportsFace
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun WatchFaceRenderer(
    watchFaceType: WatchFaceType,
    theme: ClockTheme,
    calendar: Calendar,
    is24Hour: Boolean = false,
    showSeconds: Boolean = true,
    modifier: Modifier = Modifier,
    nextAlarmText: String? = null,
    batteryPercent: Int = 85
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when (watchFaceType) {
            WatchFaceType.CYBER_CARVED_3D -> CyberCarved3DFace(calendar, theme, showSeconds)
            WatchFaceType.MOTORSPORT_M -> MotorsportMFace(calendar, theme, showSeconds)
            WatchFaceType.SPEEDMASTER_RACING -> SpeedmasterRacingFace(calendar, theme, showSeconds)
            WatchFaceType.RACING_YELLOW_TACHY -> RacingYellowTachyFace(calendar, theme, showSeconds)
            WatchFaceType.CASIO_ROYALE -> CasioRoyaleFace(calendar, theme, showSeconds)
            WatchFaceType.HAWKER_AVIATOR -> HawkerAviatorFace(calendar, theme, showSeconds)
            WatchFaceType.MECHA_TACTICAL -> MechaTacticalFace(calendar, theme, showSeconds)
            WatchFaceType.SEIKO_DIGI_ANA -> SeikoDigiAnaFace(calendar, theme, showSeconds)
            WatchFaceType.CLUB_RETROGRADE -> ClubRetrogradeFace(calendar, theme, showSeconds)
            WatchFaceType.ROTARY_GAUGE -> RotaryGaugeFace(calendar, theme, showSeconds)
            WatchFaceType.BMW_M_WHEEL -> BmwMWheelFace(calendar, theme, showSeconds)
            WatchFaceType.ORANGE_TACTICAL_DIVER -> OrangeTacticalDiverFace(calendar, theme, showSeconds)
            WatchFaceType.COCKPIT_YELLOW -> CockpitYellowFace(calendar, theme, showSeconds)
            WatchFaceType.TITANIUM_RED_CHRONO -> TitaniumRedChronoFace(calendar, theme, showSeconds)
            WatchFaceType.MODERN_PODS_SPORTS -> ModernPodsSportsFace(calendar, theme, showSeconds)

            WatchFaceType.CALIFORNIA -> CaliforniaFace(calendar, theme, showSeconds)
            WatchFaceType.CHRONOGRAPH_PRO -> ChronographProFace(calendar, theme, showSeconds)
            WatchFaceType.AVIATOR_B -> AviatorTypeBFace(calendar, theme, showSeconds)
            WatchFaceType.NAUTILUS_DIVER -> NautilusDiverFace(calendar, theme, showSeconds)
            WatchFaceType.BAUHAUS_CLASSIC -> BauhausClassicFace(calendar, theme, showSeconds)
            WatchFaceType.REGULATEUR -> RegulateurFace(calendar, theme, showSeconds)
            WatchFaceType.TOURBILLON_ROYAL -> TourbillonRoyalFace(calendar, theme, showSeconds)
            WatchFaceType.FIELD_TACTICAL_24H -> FieldTactical24HFace(calendar, theme, showSeconds)
        }
    }
}

// -------------------------------------------------------------
// 1. CALIFORNIA ANALOG FACE
// -------------------------------------------------------------
@Composable
private fun CaliforniaFace(
    calendar: Calendar,
    theme: ClockTheme,
    showSeconds: Boolean
) {
    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val millisecond = calendar.get(Calendar.MILLISECOND)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    val hourAngle = (hour * 30f) + (minute * 0.5f)
    val minuteAngle = (minute * 6f) + (second * 0.1f)
    val secondAngle = if (showSeconds) (second * 6f) + (millisecond * 0.006f) else 0f

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Canvas(modifier = Modifier.size(dialSize.dp)) {
            val radius = size.minDimension / 2f
            val center = this.center

            // Outer bezel
            drawCircle(Color(0xFF0F172A), radius, style = Stroke(8.dp.toPx()))
            drawCircle(Color(0xFF1E293B), radius - 4.dp.toPx(), style = Stroke(1.5.dp.toPx()))

            // Railway track
            val trackRadius = radius - 18.dp.toPx()
            for (i in 0 until 60) {
                val angle = i * 6f * (PI / 180f).toFloat()
                val isFive = i % 5 == 0
                val tickLen = if (isFive) 10.dp.toPx() else 4.dp.toPx()
                val p1 = Offset(center.x + trackRadius * sin(angle), center.y - trackRadius * cos(angle))
                val p2 = Offset(center.x + (trackRadius - tickLen) * sin(angle), center.y - (trackRadius - tickLen) * cos(angle))
                drawLine(
                    color = if (isFive) theme.accentColor else Color(0xFF475569),
                    start = p1,
                    end = p2,
                    strokeWidth = if (isFive) 2.5.dp.toPx() else 1.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // Stylish Visible Legends (California Dial: Roman top XII, I, II, X, XI; Arabic bottom 4, 5, 7, 8)
            val markers = listOf(
                "XII", "I", "II", "•", "4", "5", "6", "7", "8", "9", "X", "XI"
            )
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 15.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.BOLD)
                isAntiAlias = true
            }

            val numeralRadius = radius - 38.dp.toPx()
            for (idx in markers.indices) {
                if (idx == 3) continue // 3 o'clock is date window
                val angle = idx * 30f * (PI / 180f).toFloat()
                val px = center.x + numeralRadius * sin(angle)
                val py = center.y - numeralRadius * cos(angle) + 5.dp.toPx()
                drawContext.canvas.nativeCanvas.drawText(markers[idx], px, py, paint)
            }

            // Brand & Horology Legends
            val legendPaint = android.graphics.Paint().apply {
                color = theme.accentColor.toArgb()
                textSize = 9.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.BOLD)
                letterSpacing = 0.2f
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText("AURA", center.x, center.y - radius * 0.32f, legendPaint)

            legendPaint.color = android.graphics.Color.parseColor("#94A3B8")
            legendPaint.textSize = 7.5.dp.toPx()
            drawContext.canvas.nativeCanvas.drawText("AUTOMATIC", center.x, center.y + radius * 0.38f, legendPaint)

            // Date Window at 3 o'clock
            val dateBoxX = center.x + radius * 0.52f
            val dateBoxY = center.y
            drawRoundRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(dateBoxX - 13.dp.toPx(), dateBoxY - 9.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(26.dp.toPx(), 18.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
            )
            val datePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 12.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText("$dayOfMonth", dateBoxX, dateBoxY + 4.5.dp.toPx(), datePaint)

            // Hour Hand
            rotate(hourAngle, center) {
                val handLen = radius * 0.52f
                drawLine(Color.White, Offset(center.x, center.y + 12.dp.toPx()), Offset(center.x, center.y - handLen), 7.dp.toPx(), StrokeCap.Round)
                drawLine(theme.accentColor, Offset(center.x, center.y - 12.dp.toPx()), Offset(center.x, center.y - handLen + 8.dp.toPx()), 4.dp.toPx(), StrokeCap.Round)
            }

            // Minute Hand
            rotate(minuteAngle, center) {
                val handLen = radius * 0.78f
                drawLine(Color.White, Offset(center.x, center.y + 16.dp.toPx()), Offset(center.x, center.y - handLen), 5.dp.toPx(), StrokeCap.Round)
                drawLine(theme.primaryColor, Offset(center.x, center.y - 14.dp.toPx()), Offset(center.x, center.y - handLen + 8.dp.toPx()), 3.dp.toPx(), StrokeCap.Round)
            }

            // Seconds Hand
            if (showSeconds) {
                rotate(secondAngle, center) {
                    val handLen = radius * 0.88f
                    drawLine(theme.secondaryColor, Offset(center.x, center.y + 22.dp.toPx()), Offset(center.x, center.y - handLen), 2.dp.toPx(), StrokeCap.Round)
                    drawCircle(theme.secondaryColor, 4.dp.toPx(), Offset(center.x, center.y - radius * 0.6f))
                }
            }

            drawCircle(theme.primaryColor, 5.dp.toPx(), center)
            drawCircle(Color.Black, 2.dp.toPx(), center)
        }
    }
}

// -------------------------------------------------------------
// 2. CHRONOGRAPH PRO FACE
// -------------------------------------------------------------
@Composable
private fun ChronographProFace(
    calendar: Calendar,
    theme: ClockTheme,
    showSeconds: Boolean
) {
    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val millisecond = calendar.get(Calendar.MILLISECOND)

    val hourAngle = (hour * 30f) + (minute * 0.5f)
    val minuteAngle = (minute * 6f) + (second * 0.1f)
    val secondAngle = if (showSeconds) (second * 6f) + (millisecond * 0.006f) else 0f

    val topSubAngle = (minute % 30) * 12f
    val bottomSubAngle = second * 6f

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Canvas(modifier = Modifier.size(dialSize.dp)) {
            val radius = size.minDimension / 2f
            val center = this.center

            // Outer Tachymeter Ring
            drawCircle(Color(0xFF0F172A), radius, style = Stroke(18.dp.toPx()))

            // Tachymeter Numbers & Legends
            val tachyValues = listOf("60", "70", "80", "90", "100", "120", "160", "200", "240", "300", "400", "500")
            val tachyPaint = android.graphics.Paint().apply {
                color = theme.accentColor.toArgb()
                textSize = 7.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            val tachyRadius = radius - 8.dp.toPx()
            for (idx in tachyValues.indices) {
                val angle = idx * 30f * (PI / 180f).toFloat()
                val px = center.x + tachyRadius * sin(angle)
                val py = center.y - tachyRadius * cos(angle) + 2.5.dp.toPx()
                drawContext.canvas.nativeCanvas.drawText(tachyValues[idx], px, py, tachyPaint)
            }

            // Dial 1..12 Numerals
            val numPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 14.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            val dialNumRadius = radius - 32.dp.toPx()
            for (i in 1..12) {
                if (i == 12 || i == 6) continue // Subdials occupy 12 and 6
                val angle = i * 30f * (PI / 180f).toFloat()
                val px = center.x + dialNumRadius * sin(angle)
                val py = center.y - dialNumRadius * cos(angle) + 5.dp.toPx()
                drawContext.canvas.nativeCanvas.drawText("$i", px, py, numPaint)
            }

            // Brand legends
            val brandPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 9.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                letterSpacing = 0.15f
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText("CHRONOGRAPH", center.x + radius * 0.38f, center.y + 4.dp.toPx(), brandPaint)
            brandPaint.color = theme.accentColor.toArgb()
            brandPaint.textSize = 7.5.dp.toPx()
            drawContext.canvas.nativeCanvas.drawText("CHRONOMETER", center.x - radius * 0.38f, center.y + 4.dp.toPx(), brandPaint)

            // Top Sub-dial (30-Min)
            val topCenter = Offset(center.x, center.y - radius * 0.38f)
            val subRadius = radius * 0.22f
            drawCircle(Color(0xFF0B132B), subRadius, topCenter)
            drawCircle(Color(0xFF334155), subRadius, topCenter, style = Stroke(1.dp.toPx()))

            val subPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#94A3B8")
                textSize = 7.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText("30", topCenter.x, topCenter.y - subRadius + 8.dp.toPx(), subPaint)
            drawContext.canvas.nativeCanvas.drawText("15", topCenter.x + subRadius - 6.dp.toPx(), topCenter.y + 3.dp.toPx(), subPaint)

            rotate(topSubAngle, topCenter) {
                drawLine(theme.accentColor, topCenter, Offset(topCenter.x, topCenter.y - subRadius * 0.82f), 2.dp.toPx(), StrokeCap.Round)
            }

            // Bottom Sub-dial (60-Sec)
            val bottomCenter = Offset(center.x, center.y + radius * 0.38f)
            drawCircle(Color(0xFF0B132B), subRadius, bottomCenter)
            drawCircle(Color(0xFF334155), subRadius, bottomCenter, style = Stroke(1.dp.toPx()))

            drawContext.canvas.nativeCanvas.drawText("60", bottomCenter.x, bottomCenter.y - subRadius + 8.dp.toPx(), subPaint)
            drawContext.canvas.nativeCanvas.drawText("30", bottomCenter.x, bottomCenter.y + subRadius - 3.dp.toPx(), subPaint)

            rotate(bottomSubAngle, bottomCenter) {
                drawLine(theme.primaryColor, bottomCenter, Offset(bottomCenter.x, bottomCenter.y - subRadius * 0.82f), 2.dp.toPx(), StrokeCap.Round)
            }

            // Hour Hand
            rotate(hourAngle, center) {
                drawLine(Color.White, center, Offset(center.x, center.y - radius * 0.5f), 6.dp.toPx(), StrokeCap.Round)
            }

            // Minute Hand
            rotate(minuteAngle, center) {
                drawLine(Color.White, center, Offset(center.x, center.y - radius * 0.72f), 4.5.dp.toPx(), StrokeCap.Round)
            }

            // Sweep Second Hand
            if (showSeconds) {
                rotate(secondAngle, center) {
                    drawLine(theme.secondaryColor, Offset(center.x, center.y + 18.dp.toPx()), Offset(center.x, center.y - radius * 0.86f), 2.dp.toPx(), StrokeCap.Round)
                }
            }

            drawCircle(theme.secondaryColor, 5.dp.toPx(), center)
        }
    }
}

// -------------------------------------------------------------
// 3. AVIATOR TYPE-B FACE (Classic German B-Uhr)
// -------------------------------------------------------------
@Composable
private fun AviatorTypeBFace(
    calendar: Calendar,
    theme: ClockTheme,
    showSeconds: Boolean
) {
    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val millisecond = calendar.get(Calendar.MILLISECOND)

    val hourAngle = (hour * 30f) + (minute * 0.5f)
    val minuteAngle = (minute * 6f) + (second * 0.1f)
    val secondAngle = if (showSeconds) (second * 6f) + (millisecond * 0.006f) else 0f

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Canvas(modifier = Modifier.size(dialSize.dp)) {
            val radius = size.minDimension / 2f
            val center = this.center

            // Dial background circle
            drawCircle(Color(0xFF070B14), radius)
            drawCircle(Color(0xFF1E293B), radius - 2.dp.toPx(), style = Stroke(2.dp.toPx()))

            // Outer 60-Minute Scale: 55, 60, 05, 10, 15, 20, 25, 30, 35, 40, 45, 50
            val outerNumbers = listOf("60", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55")
            val outerPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 14.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            val outerNumRadius = radius - 20.dp.toPx()
            for (idx in outerNumbers.indices) {
                if (idx == 0) continue // Triangle at 12
                val angle = idx * 30f * (PI / 180f).toFloat()
                val px = center.x + outerNumRadius * sin(angle)
                val py = center.y - outerNumRadius * cos(angle) + 5.dp.toPx()
                drawContext.canvas.nativeCanvas.drawText(outerNumbers[idx], px, py, outerPaint)
            }

            // Triangle & Two Dots at 12 o'clock
            val triPath = Path().apply {
                val topY = center.y - radius + 12.dp.toPx()
                moveTo(center.x, topY)
                lineTo(center.x - 9.dp.toPx(), topY + 14.dp.toPx())
                lineTo(center.x + 9.dp.toPx(), topY + 14.dp.toPx())
                close()
            }
            drawPath(triPath, theme.primaryColor)
            drawCircle(theme.primaryColor, 2.5.dp.toPx(), Offset(center.x - 14.dp.toPx(), center.y - radius + 19.dp.toPx()))
            drawCircle(theme.primaryColor, 2.5.dp.toPx(), Offset(center.x + 14.dp.toPx(), center.y - radius + 19.dp.toPx()))

            // Inner Hour Ring (1..12)
            val innerRingRadius = radius * 0.52f
            drawCircle(Color(0xFF334155), innerRingRadius, center, style = Stroke(1.5.dp.toPx()))

            val innerPaint = android.graphics.Paint().apply {
                color = theme.accentColor.toArgb()
                textSize = 12.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            val innerNumRadius = innerRingRadius - 12.dp.toPx()
            for (i in 1..12) {
                val angle = i * 30f * (PI / 180f).toFloat()
                val px = center.x + innerNumRadius * sin(angle)
                val py = center.y - innerNumRadius * cos(angle) + 4.dp.toPx()
                drawContext.canvas.nativeCanvas.drawText("$i", px, py, innerPaint)
            }

            // Legend
            val pilotLegend = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#64748B")
                textSize = 8.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                letterSpacing = 0.2f
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText("FLIEGER B-UHR", center.x, center.y + radius * 0.28f, pilotLegend)

            // Hour Hand (Short, pointing strictly to inner circle)
            rotate(hourAngle, center) {
                val handLen = innerRingRadius - 4.dp.toPx()
                drawLine(Color.White, Offset(center.x, center.y + 10.dp.toPx()), Offset(center.x, center.y - handLen), 6.dp.toPx(), StrokeCap.Round)
                drawLine(theme.primaryColor, Offset(center.x, center.y - 8.dp.toPx()), Offset(center.x, center.y - handLen + 4.dp.toPx()), 3.dp.toPx(), StrokeCap.Round)
            }

            // Minute Hand (Long, reaching outer scale)
            rotate(minuteAngle, center) {
                val handLen = radius * 0.82f
                drawLine(Color.White, Offset(center.x, center.y + 14.dp.toPx()), Offset(center.x, center.y - handLen), 4.5.dp.toPx(), StrokeCap.Round)
                drawLine(theme.accentColor, Offset(center.x, center.y - 12.dp.toPx()), Offset(center.x, center.y - handLen + 6.dp.toPx()), 2.5.dp.toPx(), StrokeCap.Round)
            }

            // Sweep Second Hand
            if (showSeconds) {
                rotate(secondAngle, center) {
                    val handLen = radius * 0.88f
                    drawLine(theme.secondaryColor, Offset(center.x, center.y + 20.dp.toPx()), Offset(center.x, center.y - handLen), 2.dp.toPx(), StrokeCap.Round)
                }
            }

            drawCircle(theme.primaryColor, 5.dp.toPx(), center)
        }
    }
}

// -------------------------------------------------------------
// 4. NAUTILUS DIVER FACE (Luxury Embossed Diver)
// -------------------------------------------------------------
@Composable
private fun NautilusDiverFace(
    calendar: Calendar,
    theme: ClockTheme,
    showSeconds: Boolean
) {
    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val millisecond = calendar.get(Calendar.MILLISECOND)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    val hourAngle = (hour * 30f) + (minute * 0.5f)
    val minuteAngle = (minute * 6f) + (second * 0.1f)
    val secondAngle = if (showSeconds) (second * 6f) + (millisecond * 0.006f) else 0f

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Canvas(modifier = Modifier.size(dialSize.dp)) {
            val radius = size.minDimension / 2f
            val center = this.center

            // Diver Rotating Bezel
            drawCircle(Color(0xFF0F172A), radius, style = Stroke(16.dp.toPx()))

            // Bezel Markings: 10, 20, 30, 40, 50, and Lum Triangle at 60
            val bezelPaint = android.graphics.Paint().apply {
                color = theme.accentColor.toArgb()
                textSize = 9.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            val bezelRadius = radius - 8.dp.toPx()
            val bezelNumbers = mapOf(2 to "10", 4 to "20", 6 to "30", 8 to "40", 10 to "50")
            for ((idx, text) in bezelNumbers) {
                val angle = idx * 30f * (PI / 180f).toFloat()
                val px = center.x + bezelRadius * sin(angle)
                val py = center.y - bezelRadius * cos(angle) + 3.dp.toPx()
                drawContext.canvas.nativeCanvas.drawText(text, px, py, bezelPaint)
            }
            drawCircle(theme.primaryColor, 3.5.dp.toPx(), Offset(center.x, center.y - bezelRadius))

            // Dial Background with horizontal embossed grooves
            drawCircle(Color(0xFF060B14), radius - 16.dp.toPx())
            for (yOffset in -100..100 step 14) {
                val lineY = center.y + yOffset.dp.toPx()
                val dy = yOffset.dp.toPx()
                val innerR = radius - 20.dp.toPx()
                if (dy * dy < innerR * innerR) {
                    val halfW = kotlin.math.sqrt(innerR * innerR - dy * dy)
                    drawLine(Color(0xFF131D31), Offset(center.x - halfW, lineY), Offset(center.x + halfW, lineY), 1.5.dp.toPx())
                }
            }

            // Applied Luminous Pill Indices & Legends 12, 6, 9
            val dialRadius = radius - 30.dp.toPx()
            for (i in 0 until 12) {
                if (i == 3) continue // Date window at 3
                val angle = i * 30f * (PI / 180f).toFloat()
                val px = center.x + dialRadius * sin(angle)
                val py = center.y - dialRadius * cos(angle)
                val isMajor = i == 0 || i == 6 || i == 9
                drawRoundRect(
                    color = Color.White,
                    topLeft = Offset(px - 3.5.dp.toPx(), py - (if (isMajor) 7.dp.toPx() else 4.dp.toPx())),
                    size = androidx.compose.ui.geometry.Size(7.dp.toPx(), if (isMajor) 14.dp.toPx() else 8.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.dp.toPx())
                )
                drawRoundRect(
                    color = theme.primaryColor,
                    topLeft = Offset(px - 2.dp.toPx(), py - (if (isMajor) 5.dp.toPx() else 2.5.dp.toPx())),
                    size = androidx.compose.ui.geometry.Size(4.dp.toPx(), if (isMajor) 10.dp.toPx() else 5.dp.toPx()),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx())
                )
            }

            // Diver Legends
            val brandPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 10.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                letterSpacing = 0.15f
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText("AURA DIVER", center.x, center.y - radius * 0.28f, brandPaint)
            brandPaint.color = theme.accentColor.toArgb()
            brandPaint.textSize = 7.5.dp.toPx()
            drawContext.canvas.nativeCanvas.drawText("300m / 1000ft", center.x, center.y + radius * 0.32f, brandPaint)

            // Date Window at 3 o'clock
            val dateBoxX = center.x + radius * 0.48f
            drawRoundRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(dateBoxX - 12.dp.toPx(), center.y - 9.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(24.dp.toPx(), 18.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
            )
            val datePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 11.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText("$dayOfMonth", dateBoxX, center.y + 4.5.dp.toPx(), datePaint)

            // Hour Hand
            rotate(hourAngle, center) {
                drawLine(Color.White, center, Offset(center.x, center.y - radius * 0.48f), 7.dp.toPx(), StrokeCap.Round)
                drawLine(theme.primaryColor, center, Offset(center.x, center.y - radius * 0.44f), 4.dp.toPx(), StrokeCap.Round)
            }

            // Minute Hand
            rotate(minuteAngle, center) {
                drawLine(Color.White, center, Offset(center.x, center.y - radius * 0.74f), 5.dp.toPx(), StrokeCap.Round)
                drawLine(theme.accentColor, center, Offset(center.x, center.y - radius * 0.7f), 3.dp.toPx(), StrokeCap.Round)
            }

            // Second Hand
            if (showSeconds) {
                rotate(secondAngle, center) {
                    drawLine(theme.secondaryColor, Offset(center.x, center.y + 20.dp.toPx()), Offset(center.x, center.y - radius * 0.84f), 2.dp.toPx(), StrokeCap.Round)
                }
            }

            drawCircle(theme.secondaryColor, 5.dp.toPx(), center)
        }
    }
}

// -------------------------------------------------------------
// 5. BAUHAUS CLASSIC FACE (Clean German Horology)
// -------------------------------------------------------------
@Composable
private fun BauhausClassicFace(
    calendar: Calendar,
    theme: ClockTheme,
    showSeconds: Boolean
) {
    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val millisecond = calendar.get(Calendar.MILLISECOND)

    val hourAngle = (hour * 30f) + (minute * 0.5f)
    val minuteAngle = (minute * 6f) + (second * 0.1f)
    val secondAngle = if (showSeconds) (second * 6f) + (millisecond * 0.006f) else 0f

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Canvas(modifier = Modifier.size(dialSize.dp)) {
            val radius = size.minDimension / 2f
            val center = this.center

            // Outer dial border
            drawCircle(Color(0xFF0F172A), radius, style = Stroke(4.dp.toPx()))

            // Clean hour numerals 1..12 in crisp Bauhaus typography
            val numPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 16.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.NORMAL)
                isAntiAlias = true
            }
            val numRadius = radius - 26.dp.toPx()
            for (i in 1..12) {
                if (i == 6) continue // Small seconds subdial at 6
                val angle = i * 30f * (PI / 180f).toFloat()
                val px = center.x + numRadius * sin(angle)
                val py = center.y - numRadius * cos(angle) + 6.dp.toPx()
                drawContext.canvas.nativeCanvas.drawText("$i", px, py, numPaint)
            }

            // Small Seconds Sub-dial at 6 o'clock
            val secCenter = Offset(center.x, center.y + radius * 0.44f)
            val secRadius = radius * 0.22f
            drawCircle(Color(0xFF090E17), secRadius, secCenter)
            drawCircle(Color(0xFF1E293B), secRadius, secCenter, style = Stroke(1.dp.toPx()))

            val subPaint = android.graphics.Paint().apply {
                color = theme.accentColor.toArgb()
                textSize = 7.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText("60", secCenter.x, secCenter.y - secRadius + 7.dp.toPx(), subPaint)
            drawContext.canvas.nativeCanvas.drawText("30", secCenter.x, secCenter.y + secRadius - 2.5.dp.toPx(), subPaint)

            rotate(secondAngle, secCenter) {
                drawLine(theme.secondaryColor, secCenter, Offset(secCenter.x, secCenter.y - secRadius * 0.85f), 1.8.dp.toPx(), StrokeCap.Round)
            }

            // Brand Legends
            val brandPaint = android.graphics.Paint().apply {
                color = theme.primaryColor.toArgb()
                textSize = 10.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                letterSpacing = 0.25f
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText("AUTOMATIK", center.x, center.y - radius * 0.35f, brandPaint)

            // Slender Baton Hour Hand
            rotate(hourAngle, center) {
                drawLine(Color.White, center, Offset(center.x, center.y - radius * 0.48f), 4.5.dp.toPx(), StrokeCap.Round)
            }

            // Slender Baton Minute Hand
            rotate(minuteAngle, center) {
                drawLine(theme.accentColor, center, Offset(center.x, center.y - radius * 0.78f), 3.dp.toPx(), StrokeCap.Round)
            }

            drawCircle(Color.White, 4.dp.toPx(), center)
        }
    }
}

// -------------------------------------------------------------
// 6. REGULATEUR FACE (Classic Master Regulator)
// -------------------------------------------------------------
@Composable
private fun RegulateurFace(
    calendar: Calendar,
    theme: ClockTheme,
    showSeconds: Boolean
) {
    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val millisecond = calendar.get(Calendar.MILLISECOND)

    val hourSubAngle = (hour * 30f) + (minute * 0.5f)
    val mainMinuteAngle = (minute * 6f) + (second * 0.1f)
    val secondSubAngle = if (showSeconds) (second * 6f) + (millisecond * 0.006f) else 0f

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Canvas(modifier = Modifier.size(dialSize.dp)) {
            val radius = size.minDimension / 2f
            val center = this.center

            // Outer dial border
            drawCircle(Color(0xFF0F172A), radius, style = Stroke(6.dp.toPx()))

            // Outer 60-Minute Scale (05..60) swept by the master central minute hand
            val minPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 11.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            val minRadius = radius - 16.dp.toPx()
            val minLabels = listOf("60", "05", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55")
            for (idx in minLabels.indices) {
                val angle = idx * 30f * (PI / 180f).toFloat()
                val px = center.x + minRadius * sin(angle)
                val py = center.y - minRadius * cos(angle) + 4.dp.toPx()
                drawContext.canvas.nativeCanvas.drawText(minLabels[idx], px, py, minPaint)
            }

            // Top Decentralized Hour Dial at 12 o'clock with Roman numerals
            val hourCenter = Offset(center.x, center.y - radius * 0.38f)
            val subRadius = radius * 0.28f
            drawCircle(Color(0xFF090E17), subRadius, hourCenter)
            drawCircle(Color(0xFF1E293B), subRadius, hourCenter, style = Stroke(1.5.dp.toPx()))

            val romanPaint = android.graphics.Paint().apply {
                color = theme.accentColor.toArgb()
                textSize = 9.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.BOLD)
                isAntiAlias = true
            }
            val romanNumerals = listOf("XII", "III", "VI", "IX")
            val romanAngles = listOf(0f, 90f, 180f, 270f)
            for (i in romanNumerals.indices) {
                val angle = romanAngles[i] * (PI / 180f).toFloat()
                val px = hourCenter.x + (subRadius - 8.dp.toPx()) * sin(angle)
                val py = hourCenter.y - (subRadius - 8.dp.toPx()) * cos(angle) + 3.dp.toPx()
                drawContext.canvas.nativeCanvas.drawText(romanNumerals[i], px, py, romanPaint)
            }

            rotate(hourSubAngle, hourCenter) {
                drawLine(Color.White, hourCenter, Offset(hourCenter.x, hourCenter.y - subRadius * 0.8f), 3.5.dp.toPx(), StrokeCap.Round)
            }

            // Bottom Decentralized Seconds Dial at 6 o'clock
            val secCenter = Offset(center.x, center.y + radius * 0.38f)
            drawCircle(Color(0xFF090E17), subRadius, secCenter)
            drawCircle(Color(0xFF1E293B), subRadius, secCenter, style = Stroke(1.5.dp.toPx()))

            val secPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#94A3B8")
                textSize = 8.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText("60", secCenter.x, secCenter.y - subRadius + 9.dp.toPx(), secPaint)
            drawContext.canvas.nativeCanvas.drawText("30", secCenter.x, secCenter.y + subRadius - 3.dp.toPx(), secPaint)

            rotate(secondSubAngle, secCenter) {
                drawLine(theme.secondaryColor, secCenter, Offset(secCenter.x, secCenter.y - subRadius * 0.82f), 1.8.dp.toPx(), StrokeCap.Round)
            }

            // Master Dial Legend
            val regPaint = android.graphics.Paint().apply {
                color = theme.primaryColor.toArgb()
                textSize = 9.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                letterSpacing = 0.2f
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText("RÉGULATEUR", center.x, center.y + 4.dp.toPx(), regPaint)

            // Central Master Minute Hand (Full length)
            rotate(mainMinuteAngle, center) {
                val handLen = radius * 0.86f
                drawLine(Color.White, Offset(center.x, center.y + 16.dp.toPx()), Offset(center.x, center.y - handLen), 4.5.dp.toPx(), StrokeCap.Round)
                drawLine(theme.primaryColor, Offset(center.x, center.y - 12.dp.toPx()), Offset(center.x, center.y - handLen + 8.dp.toPx()), 2.5.dp.toPx(), StrokeCap.Round)
            }

            drawCircle(theme.primaryColor, 6.dp.toPx(), center)
        }
    }
}

// -------------------------------------------------------------
// 7. TOURBILLON ROYAL FACE (Haute Horlogerie Open-Heart)
// -------------------------------------------------------------
@Composable
private fun TourbillonRoyalFace(
    calendar: Calendar,
    theme: ClockTheme,
    showSeconds: Boolean
) {
    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val millisecond = calendar.get(Calendar.MILLISECOND)

    val hourAngle = (hour * 30f) + (minute * 0.5f)
    val minuteAngle = (minute * 6f) + (second * 0.1f)
    val tourbillonRotation = (second * 6f) + (millisecond * 0.006f)

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Canvas(modifier = Modifier.size(dialSize.dp)) {
            val radius = size.minDimension / 2f
            val center = this.center

            // Outer fluted bezel ring
            drawCircle(Color(0xFF0F172A), radius, style = Stroke(8.dp.toPx()))

            // Radial Serif Roman Numerals I..XII
            val romanNumerals = listOf("XII", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI")
            val romanPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 15.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.BOLD)
                isAntiAlias = true
            }
            val numRadius = radius - 26.dp.toPx()
            for (idx in romanNumerals.indices) {
                if (idx == 6) continue // Tourbillon cage at 6 o'clock
                val angle = idx * 30f * (PI / 180f).toFloat()
                val px = center.x + numRadius * sin(angle)
                val py = center.y - numRadius * cos(angle) + 5.dp.toPx()
                drawContext.canvas.nativeCanvas.drawText(romanNumerals[idx], px, py, romanPaint)
            }

            // Guilloché concentric pattern in center
            for (r in 20..60 step 10) {
                drawCircle(Color(0xFF1E293B).copy(alpha = 0.5f), r.dp.toPx(), center, style = Stroke(1.dp.toPx()))
            }

            // Tourbillon Open-Heart Bridge at 6 o'clock
            val tourbillonCenter = Offset(center.x, center.y + radius * 0.44f)
            val tourbillonRadius = radius * 0.26f
            drawCircle(Color(0xFF070B14), tourbillonRadius, tourbillonCenter)
            drawCircle(theme.accentColor, tourbillonRadius, tourbillonCenter, style = Stroke(2.dp.toPx()))

            // Rotating 3-arm Tourbillon Carriage
            rotate(tourbillonRotation, tourbillonCenter) {
                for (arm in 0 until 3) {
                    val angle = arm * 120f * (PI / 180f).toFloat()
                    val pEnd = Offset(tourbillonCenter.x + tourbillonRadius * 0.85f * sin(angle), tourbillonCenter.y - tourbillonRadius * 0.85f * cos(angle))
                    drawLine(theme.primaryColor, tourbillonCenter, pEnd, 2.5.dp.toPx(), StrokeCap.Round)
                }
                // Center ruby balance jewel
                drawCircle(Color(0xFFFF2A48), 4.5.dp.toPx(), tourbillonCenter)
            }

            // Royal Brand Legend
            val legendPaint = android.graphics.Paint().apply {
                color = theme.accentColor.toArgb()
                textSize = 9.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.create(android.graphics.Typeface.SERIF, android.graphics.Typeface.BOLD)
                letterSpacing = 0.2f
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText("TOURBILLON • 24J", center.x, center.y - radius * 0.32f, legendPaint)

            // Faceted Dauphine Hour Hand
            rotate(hourAngle, center) {
                val handLen = radius * 0.48f
                val path = Path().apply {
                    moveTo(center.x, center.y + 8.dp.toPx())
                    lineTo(center.x - 5.dp.toPx(), center.y - handLen * 0.5f)
                    lineTo(center.x, center.y - handLen)
                    lineTo(center.x + 5.dp.toPx(), center.y - handLen * 0.5f)
                    close()
                }
                drawPath(path, Color.White)
            }

            // Faceted Dauphine Minute Hand
            rotate(minuteAngle, center) {
                val handLen = radius * 0.74f
                val path = Path().apply {
                    moveTo(center.x, center.y + 10.dp.toPx())
                    lineTo(center.x - 4.dp.toPx(), center.y - handLen * 0.5f)
                    lineTo(center.x, center.y - handLen)
                    lineTo(center.x + 4.dp.toPx(), center.y - handLen * 0.5f)
                    close()
                }
                drawPath(path, theme.primaryColor)
            }

            drawCircle(Color.White, 4.5.dp.toPx(), center)
        }
    }
}

// -------------------------------------------------------------
// 8. FIELD TACTICAL 24H FACE (Military Dual Scale)
// -------------------------------------------------------------
@Composable
private fun FieldTactical24HFace(
    calendar: Calendar,
    theme: ClockTheme,
    showSeconds: Boolean
) {
    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val millisecond = calendar.get(Calendar.MILLISECOND)
    val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

    val hourAngle = (hour * 30f) + (minute * 0.5f)
    val minuteAngle = (minute * 6f) + (second * 0.1f)
    val secondAngle = if (showSeconds) (second * 6f) + (millisecond * 0.006f) else 0f

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Canvas(modifier = Modifier.size(dialSize.dp)) {
            val radius = size.minDimension / 2f
            val center = this.center

            // Outer matte bezel
            drawCircle(Color(0xFF0F172A), radius, style = Stroke(6.dp.toPx()))

            // Outer 12-Hour Numerals (1..12) in high contrast white
            val outPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 15.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            val outRadius = radius - 26.dp.toPx()
            for (i in 1..12) {
                if (i == 3) continue // Date window at 3
                val angle = i * 30f * (PI / 180f).toFloat()
                val px = center.x + outRadius * sin(angle)
                val py = center.y - outRadius * cos(angle) + 5.5.dp.toPx()
                drawContext.canvas.nativeCanvas.drawText("$i", px, py, outPaint)
            }

            // Inner 24-Hour Tactical Scale (13..24) in theme accent color
            val inPaint = android.graphics.Paint().apply {
                color = theme.accentColor.toArgb()
                textSize = 9.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            val inRadius = radius * 0.52f
            drawCircle(Color(0xFF1E293B), inRadius + 6.dp.toPx(), center, style = Stroke(1.dp.toPx()))

            for (i in 1..12) {
                val milHour = i + 12
                val angle = i * 30f * (PI / 180f).toFloat()
                val px = center.x + inRadius * sin(angle)
                val py = center.y - inRadius * cos(angle) + 3.dp.toPx()
                drawContext.canvas.nativeCanvas.drawText("$milHour", px, py, inPaint)
            }

            // Military Spec Legends
            val milPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#94A3B8")
                textSize = 8.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                letterSpacing = 0.2f
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText("TACTICAL SPEC", center.x, center.y - radius * 0.28f, milPaint)
            milPaint.color = theme.secondaryColor.toArgb()
            drawContext.canvas.nativeCanvas.drawText("24H MIL", center.x, center.y + radius * 0.28f, milPaint)

            // Date Window at 3 o'clock
            val dateBoxX = center.x + radius * 0.52f
            drawRoundRect(
                color = Color(0xFF1E293B),
                topLeft = Offset(dateBoxX - 12.dp.toPx(), center.y - 8.5.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(24.dp.toPx(), 17.dp.toPx()),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(3.5.dp.toPx())
            )
            val datePaint = android.graphics.Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 11.dp.toPx()
                textAlign = android.graphics.Paint.Align.CENTER
                typeface = android.graphics.Typeface.DEFAULT_BOLD
                isAntiAlias = true
            }
            drawContext.canvas.nativeCanvas.drawText("$dayOfMonth", dateBoxX, center.y + 4.dp.toPx(), datePaint)

            // Syringe Hour Hand
            rotate(hourAngle, center) {
                val handLen = radius * 0.5f
                drawLine(Color.White, center, Offset(center.x, center.y - handLen), 6.dp.toPx(), StrokeCap.Square)
                drawLine(theme.primaryColor, center, Offset(center.x, center.y - handLen * 0.7f), 3.dp.toPx(), StrokeCap.Square)
            }

            // Syringe Minute Hand
            rotate(minuteAngle, center) {
                val handLen = radius * 0.76f
                drawLine(Color.White, center, Offset(center.x, center.y - handLen), 4.5.dp.toPx(), StrokeCap.Square)
                drawLine(theme.accentColor, center, Offset(center.x, center.y - handLen * 0.7f), 2.5.dp.toPx(), StrokeCap.Square)
            }

            // Second Hand with Arrow Counterbalance
            if (showSeconds) {
                rotate(secondAngle, center) {
                    val handLen = radius * 0.86f
                    drawLine(theme.secondaryColor, Offset(center.x, center.y + 20.dp.toPx()), Offset(center.x, center.y - handLen), 2.dp.toPx(), StrokeCap.Round)
                    drawCircle(theme.secondaryColor, 3.5.dp.toPx(), Offset(center.x, center.y - radius * 0.6f))
                }
            }

            drawCircle(theme.secondaryColor, 5.dp.toPx(), center)
        }
    }
}
