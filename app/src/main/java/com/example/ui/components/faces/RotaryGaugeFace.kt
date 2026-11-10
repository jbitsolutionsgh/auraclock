package com.example.ui.components.faces

import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.ClockTheme
import java.util.Calendar
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun RotaryGaugeFace(
    calendar: Calendar,
    theme: ClockTheme,
    showSeconds: Boolean
) {
    val hour12 = calendar.get(Calendar.HOUR).let { if (it == 0) 12 else it }
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val isPm = calendar.get(Calendar.AM_PM) == Calendar.PM

    val minuteProgress = (minute + second / 60f) / 60f

    val blueAccent = Color(0xFF00C3FF)

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Box(
            modifier = Modifier
                .size(dialSize.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 1. Authentic High-Res Rotary Gauge Base Image
            Image(
                painter = painterResource(id = R.drawable.face_rotary_gauge),
                contentDescription = "Rotary Gauge Sports Watch Face",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Overlaid moving parts matching rotary gauge styling
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f
                val center = this.center

                // Top Minute Drum Sweeping Needle Indicator
                val topArcRadius = radius * 0.72f
                val drumThickness = 22.dp.toPx()
                // Top arc spans roughly -140 deg to -40 deg
                val activeMinDeg = -140f + (minuteProgress * 100f)
                val minRad = activeMinDeg * (PI / 180f).toFloat()
                val minP1 = Offset(center.x + (topArcRadius - drumThickness / 2f) * cos(minRad), center.y + (topArcRadius - drumThickness / 2f) * sin(minRad))
                val minP2 = Offset(center.x + (topArcRadius + drumThickness / 2f) * cos(minRad), center.y + (topArcRadius + drumThickness / 2f) * sin(minRad))
                drawLine(
                    color = blueAccent,
                    start = minP1,
                    end = minP2,
                    strokeWidth = 3.5.dp.toPx(),
                    cap = StrokeCap.Round
                )

                // Center Aperture: Live Bold Digital Hour in the Machined Hub
                val hourPaint = android.graphics.Paint().apply {
                    color = 0xFFFFFFFF.toInt()
                    textSize = radius * 0.32f
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.SANS_SERIF, android.graphics.Typeface.BOLD)
                    isAntiAlias = true
                }
                drawContext.canvas.nativeCanvas.drawText("$hour12", center.x, center.y + radius * 0.08f, hourPaint)

                // AM / PM Label above hour
                val amPmPaint = android.graphics.Paint().apply {
                    color = 0xFF94A3B8.toInt()
                    textSize = radius * 0.065f
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.DEFAULT_BOLD
                    isAntiAlias = true
                }
                drawContext.canvas.nativeCanvas.drawText(if (isPm) "PM" else "AM", center.x, center.y - radius * 0.16f, amPmPaint)

                // Bottom meter active indicator needle (moving with seconds)
                if (showSeconds) {
                    val botArcRadius = radius * 0.76f
                    val botDeg = 40f + (100f * (second / 60f))
                    val botRad = botDeg * (PI / 180f).toFloat()
                    val bp1 = Offset(center.x + (botArcRadius - 6.dp.toPx()) * cos(botRad), center.y + (botArcRadius - 6.dp.toPx()) * sin(botRad))
                    val bp2 = Offset(center.x + (botArcRadius + 6.dp.toPx()) * cos(botRad), center.y + (botArcRadius + 6.dp.toPx()) * sin(botRad))
                    drawLine(blueAccent, bp1, bp2, strokeWidth = 3.dp.toPx(), cap = StrokeCap.Round)
                }
            }
        }
    }
}
