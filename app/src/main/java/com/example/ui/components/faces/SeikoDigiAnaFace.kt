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
fun SeikoDigiAnaFace(
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
    val secondAngle = (second * 6f)

    val lcdText = Color(0xFF14241B)

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Box(
            modifier = Modifier
                .size(dialSize.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 1. Authentic High-Res Seiko Digi-Ana Dial Image
            Image(
                painter = painterResource(id = R.drawable.face_seiko_digiana),
                contentDescription = "Vintage Seiko Digi-Ana Watch Face",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Overlaid moving parts matching authentic Seiko Digi-Ana design
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f
                val center = this.center

                // Top-Right Analogue Watch Sub-dial center
                val analogCenter = Offset(center.x + radius * 0.40f, center.y - radius * 0.35f)
                val analogR = radius * 0.28f

                // White Hour Hand
                rotate(hourAngle, pivot = analogCenter) {
                    drawLine(
                        color = Color.White,
                        start = analogCenter,
                        end = Offset(analogCenter.x, analogCenter.y - analogR * 0.54f),
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Square
                    )
                }

                // White Minute Hand
                rotate(minuteAngle, pivot = analogCenter) {
                    drawLine(
                        color = Color.White,
                        start = analogCenter,
                        end = Offset(analogCenter.x, analogCenter.y - analogR * 0.82f),
                        strokeWidth = 1.8.dp.toPx(),
                        cap = StrokeCap.Square
                    )
                }

                // Gold Sweeping Second Hand
                if (showSeconds) {
                    rotate(secondAngle, pivot = analogCenter) {
                        drawLine(
                            color = Color(0xFFE5C058),
                            start = Offset(analogCenter.x, analogCenter.y + 6.dp.toPx()),
                            end = Offset(analogCenter.x, analogCenter.y - analogR * 0.88f),
                            strokeWidth = 1.2.dp.toPx()
                        )
                    }
                }
                drawCircle(Color.White, 3.dp.toPx(), analogCenter)

                // Bottom LCD Digital Display
                val botLcdY = center.y + radius * 0.28f

                // Big 7-Segment Time Display
                val lcdTimePaint = android.graphics.Paint().apply {
                    color = lcdText.toArgb()
                    textSize = radius * 0.25f
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
                    letterSpacing = 0.08f
                    isAntiAlias = true
                }
                val timeStr = String.format(Locale.getDefault(), "%02d:%02d", hour24, minute)
                drawContext.canvas.nativeCanvas.drawText(timeStr, center.x - radius * 0.12f, botLcdY + radius * 0.12f, lcdTimePaint)

                // Seconds readout
                if (showSeconds) {
                    val secPaint = android.graphics.Paint().apply {
                        color = lcdText.toArgb()
                        textSize = radius * 0.17f
                        textAlign = android.graphics.Paint.Align.LEFT
                        typeface = android.graphics.Typeface.create(android.graphics.Typeface.MONOSPACE, android.graphics.Typeface.BOLD)
                        isAntiAlias = true
                    }
                    val secStr = String.format(Locale.getDefault(), "%02d", second)
                    drawContext.canvas.nativeCanvas.drawText(secStr, center.x + radius * 0.28f, botLcdY + radius * 0.05f, secPaint)
                }
            }
        }
    }
}
