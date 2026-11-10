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
fun SpeedmasterRacingFace(
    calendar: Calendar,
    theme: ClockTheme,
    showSeconds: Boolean
) {
    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    val millisecond = calendar.get(Calendar.MILLISECOND)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val hourAngle = (hour * 30f) + (minute * 0.5f)
    val minuteAngle = (minute * 6f) + (second * 0.1f)
    val secondAngle = if (showSeconds) (second * 6f) + (millisecond * 0.006f) else 0f
    val subSecondsAngle = (second * 6f) + (millisecond * 0.006f)
    val chronoMinsAngle = (minute * 6f)

    val speedmasterGold = Color(0xFFD4AF37)

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Box(
            modifier = Modifier
                .size(dialSize.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 1. Authentic Omega Speedmaster Honeycomb Photo Dial
            Image(
                painter = painterResource(id = R.drawable.face_speedmaster_racing),
                contentDescription = "Speedmaster Racing Watch Face",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Overlaid moving hands & live complications
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f
                val center = this.center

                // Sub-dials centers
                val sub9X = center.x - radius * 0.38f
                val sub3X = center.x + radius * 0.38f
                val subY = center.y
                val subRadius = radius * 0.22f

                // Left sub-dial (Running Seconds): live rotating gold needle
                rotate(subSecondsAngle, pivot = Offset(sub9X, subY)) {
                    drawLine(
                        color = speedmasterGold,
                        start = Offset(sub9X, subY + 3.dp.toPx()),
                        end = Offset(sub9X, subY - subRadius * 0.75f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawCircle(speedmasterGold, 2.5.dp.toPx(), Offset(sub9X, subY))
                    drawCircle(Color.Black, 1.dp.toPx(), Offset(sub9X, subY))
                }

                // Right sub-dial (Chrono Minutes): live rotating gold needle
                rotate(chronoMinsAngle, pivot = Offset(sub3X, subY)) {
                    drawLine(
                        color = speedmasterGold,
                        start = Offset(sub3X, subY + 3.dp.toPx()),
                        end = Offset(sub3X, subY - subRadius * 0.75f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawCircle(speedmasterGold, 2.5.dp.toPx(), Offset(sub3X, subY))
                    drawCircle(Color.Black, 1.dp.toPx(), Offset(sub3X, subY))
                }

                // Live Date in 6 o'clock Date Aperture
                val dateY = center.y + radius * 0.52f
                val datePaint = android.graphics.Paint().apply {
                    color = 0xFFFFFFFF.toInt()
                    textSize = radius * 0.065f
                    textAlign = android.graphics.Paint.Align.CENTER
                    typeface = android.graphics.Typeface.create(android.graphics.Typeface.DEFAULT, android.graphics.Typeface.BOLD)
                }
                drawContext.canvas.nativeCanvas.drawText(String.format(Locale.getDefault(), "%02d", day), center.x, dateY + datePaint.textSize / 3f, datePaint)

                // Classic Speedmaster Spear Hands
                // Hour Hand
                rotate(hourAngle, pivot = center) {
                    val hLen = radius * 0.48f
                    drawLine(Color.Black.copy(alpha = 0.5f), Offset(center.x + 2.dp.toPx(), center.y + 12.dp.toPx()), Offset(center.x + 2.dp.toPx(), center.y - hLen + 2.dp.toPx()), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Round)
                    drawLine(speedmasterGold, Offset(center.x, center.y + 10.dp.toPx()), Offset(center.x, center.y - hLen), strokeWidth = 4.8.dp.toPx(), cap = StrokeCap.Round)
                    drawLine(Color(0xFFFFFBEB), Offset(center.x, center.y - 10.dp.toPx()), Offset(center.x, center.y - hLen + 4.dp.toPx()), strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Round)
                }

                // Minute Hand
                rotate(minuteAngle, pivot = center) {
                    val mLen = radius * 0.72f
                    drawLine(Color.Black.copy(alpha = 0.5f), Offset(center.x + 2.dp.toPx(), center.y + 14.dp.toPx()), Offset(center.x + 2.dp.toPx(), center.y - mLen + 2.dp.toPx()), strokeWidth = 4.dp.toPx(), cap = StrokeCap.Round)
                    drawLine(speedmasterGold, Offset(center.x, center.y + 12.dp.toPx()), Offset(center.x, center.y - mLen), strokeWidth = 3.8.dp.toPx(), cap = StrokeCap.Round)
                    drawLine(Color(0xFFFFFBEB), Offset(center.x, center.y - 12.dp.toPx()), Offset(center.x, center.y - mLen + 4.dp.toPx()), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
                }

                // Chronograph Central Seconds Hand with diamond counterweight
                if (showSeconds) {
                    rotate(secondAngle, pivot = center) {
                        val sLen = radius * 0.86f
                        drawLine(speedmasterGold, Offset(center.x, center.y + 20.dp.toPx()), Offset(center.x, center.y - sLen), strokeWidth = 1.6.dp.toPx(), cap = StrokeCap.Round)
                        drawCircle(speedmasterGold, 3.dp.toPx(), Offset(center.x, center.y - sLen * 0.75f))
                        drawCircle(Color.Black, 1.2.dp.toPx(), Offset(center.x, center.y - sLen * 0.75f))
                    }
                }

                // Center Pinion Cap
                drawCircle(speedmasterGold, 6.dp.toPx(), center)
                drawCircle(Color(0xFF000000), 2.5.dp.toPx(), center)
            }
        }
    }
}
