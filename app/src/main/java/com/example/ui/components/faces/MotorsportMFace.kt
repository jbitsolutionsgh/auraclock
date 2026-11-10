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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.ClockTheme
import java.util.Calendar
import kotlin.math.min

@Composable
fun MotorsportMFace(
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

        Box(
            modifier = Modifier
                .size(dialSize.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 1. Authentic BMW M Sunburst Photo Dial
            Image(
                painter = painterResource(id = R.drawable.face_motorsport_m),
                contentDescription = "Motorsport M Watch Face",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Overlaid moving metallic hands
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f
                val center = this.center

                // Polished Silver Metallic Hour Baton with Luminous Center
                rotate(hourAngle, pivot = center) {
                    val hLen = radius * 0.50f
                    // Shadow
                    drawLine(Color.Black.copy(alpha = 0.55f), Offset(center.x + 2.dp.toPx(), center.y + 12.dp.toPx()), Offset(center.x + 2.dp.toPx(), center.y - hLen + 2.dp.toPx()), strokeWidth = 7.dp.toPx(), cap = StrokeCap.Square)
                    // Silver frame
                    drawLine(Color(0xFFE2E8F0), Offset(center.x, center.y + 10.dp.toPx()), Offset(center.x, center.y - hLen), strokeWidth = 6.5.dp.toPx(), cap = StrokeCap.Square)
                    // White luminous insert
                    drawLine(Color.White, Offset(center.x, center.y - 8.dp.toPx()), Offset(center.x, center.y - hLen + 4.dp.toPx()), strokeWidth = 2.8.dp.toPx(), cap = StrokeCap.Square)
                }

                // Polished Silver Minute Baton
                rotate(minuteAngle, pivot = center) {
                    val mLen = radius * 0.74f
                    drawLine(Color.Black.copy(alpha = 0.55f), Offset(center.x + 2.dp.toPx(), center.y + 14.dp.toPx()), Offset(center.x + 2.dp.toPx(), center.y - mLen + 2.dp.toPx()), strokeWidth = 5.dp.toPx(), cap = StrokeCap.Square)
                    drawLine(Color(0xFFE2E8F0), Offset(center.x, center.y + 12.dp.toPx()), Offset(center.x, center.y - mLen), strokeWidth = 4.8.dp.toPx(), cap = StrokeCap.Square)
                    drawLine(Color.White, Offset(center.x, center.y - 10.dp.toPx()), Offset(center.x, center.y - mLen + 4.dp.toPx()), strokeWidth = 2.2.dp.toPx(), cap = StrokeCap.Square)
                }

                // BMW M-Red Sweep Seconds Needle with counterweight
                if (showSeconds) {
                    rotate(secondAngle, pivot = center) {
                        val sLen = radius * 0.86f
                        // Counterbalance tail
                        drawLine(Color(0xFFE00424), Offset(center.x, center.y + 24.dp.toPx()), Offset(center.x, center.y - sLen), strokeWidth = 1.8.dp.toPx(), cap = StrokeCap.Round)
                        drawCircle(Color(0xFF0F172A), 4.dp.toPx(), Offset(center.x, center.y + 12.dp.toPx()))
                        drawCircle(Color(0xFFE00424), 2.5.dp.toPx(), Offset(center.x, center.y + 12.dp.toPx()))
                    }
                }

                // Chrome Center Pinion
                drawCircle(Color(0xFF0F172A), 6.5.dp.toPx(), center)
                drawCircle(Color(0xFFE2E8F0), 3.5.dp.toPx(), center)
            }
        }
    }
}
