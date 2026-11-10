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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.ClockTheme
import java.util.Calendar
import java.util.Locale
import kotlin.math.min

@Composable
fun CasioRoyaleFace(
    calendar: Calendar,
    theme: ClockTheme,
    showSeconds: Boolean
) {
    val hour = calendar.get(Calendar.HOUR)
    val hour24 = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val month = calendar.get(Calendar.MONTH) + 1
    val isPm = calendar.get(Calendar.AM_PM) == Calendar.PM

    val hourAngle = (hour * 30f) + (minute * 0.5f)
    val minuteAngle = (minute * 6f) + (second * 0.1f)

    val lcdSegmentColor = Color(0xFF1E2822)

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Box(
            modifier = Modifier
                .size(dialSize.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 1. Authentic High-Res Casio Royale Dial Image
            Image(
                painter = painterResource(id = R.drawable.face_casio_royale),
                contentDescription = "Casio Royale AE-1200 Watch Face",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Overlaid Live Radar Analog Clock & Digital Time Display
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f
                val center = this.center

                // Radar Analog Clock position (top right quadrant)
                val radarCenter = Offset(center.x + radius * 0.40f, center.y - radius * 0.28f)
                val radarRadius = radius * 0.28f

                // Live LCD Analog Hour Hand
                rotate(hourAngle, pivot = radarCenter) {
                    drawLine(
                        color = lcdSegmentColor,
                        start = radarCenter,
                        end = Offset(radarCenter.x, radarCenter.y - radarRadius * 0.56f),
                        strokeWidth = 3.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Live LCD Analog Minute Hand
                rotate(minuteAngle, pivot = radarCenter) {
                    drawLine(
                        color = lcdSegmentColor,
                        start = radarCenter,
                        end = Offset(radarCenter.x, radarCenter.y - radarRadius * 0.82f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                drawCircle(lcdSegmentColor, 3.5.dp.toPx(), radarCenter)

                // Live Digital Time Readout on the Bottom LCD Window
                val botLcdY = center.y + radius * 0.25f
                val timePaint = android.graphics.Paint().apply {
                    color = lcdSegmentColor.toArgb()
                    textSize = radius * 0.24f
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
                    letterSpacing = 0.08f
                    isAntiAlias = true
                }
                val timeStr = String.format(Locale.getDefault(), "%02d:%02d", hour24, minute)
                drawContext.canvas.nativeCanvas.drawText(timeStr, center.x - radius * 0.12f, botLcdY + radius * 0.14f, timePaint)

                // Seconds in smaller font to the right
                if (showSeconds) {
                    val secPaint = android.graphics.Paint().apply {
                        color = lcdSegmentColor.toArgb()
                        textSize = radius * 0.16f
                        textAlign = android.graphics.Paint.Align.LEFT
                        typeface = android.graphics.Typeface.create(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
                        isAntiAlias = true
                    }
                    val secStr = String.format(Locale.getDefault(), "%02d", second)
                    drawContext.canvas.nativeCanvas.drawText(secStr, center.x + radius * 0.28f, botLcdY + radius * 0.06f, secPaint)
                }
            }
        }
    }
}
