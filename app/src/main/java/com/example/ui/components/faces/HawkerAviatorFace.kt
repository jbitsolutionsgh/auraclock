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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.ClockTheme
import java.util.Calendar
import kotlin.math.min

@Composable
fun HawkerAviatorFace(
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
    val subSecondsAngle = (second * 6f) + (millisecond * 0.006f)

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Box(
            modifier = Modifier
                .size(dialSize.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 1. Authentic High-Res Hawker Hunter Dial Base Image
            Image(
                painter = painterResource(id = R.drawable.face_hawker_hunter),
                contentDescription = "Hawker Cockpit Aviator Watch Face",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Overlaid moving parts matching authentic aviation styling
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f
                val center = this.center

                // Running Seconds Needle on the bottom dual-tone sub-dial
                val botSubCenter = Offset(center.x, center.y + radius * 0.40f)
                val botSubRadius = radius * 0.22f
                rotate(subSecondsAngle, pivot = botSubCenter) {
                    drawLine(
                        color = Color(0xFFEF4444),
                        start = botSubCenter,
                        end = Offset(botSubCenter.x, botSubCenter.y - botSubRadius * 0.75f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawCircle(Color(0xFF0F1A2E), 2.5.dp.toPx(), botSubCenter)
                }

                // Cockpit Hollow Arrow Hour Hand (White skeleton with dark interior)
                rotate(hourAngle, pivot = center) {
                    val hLen = radius * 0.46f
                    val hPath = Path().apply {
                        moveTo(center.x, center.y - hLen)
                        lineTo(center.x - 7.dp.toPx(), center.y - hLen + 15.dp.toPx())
                        lineTo(center.x - 4.dp.toPx(), center.y + 8.dp.toPx())
                        lineTo(center.x + 4.dp.toPx(), center.y + 8.dp.toPx())
                        lineTo(center.x + 7.dp.toPx(), center.y - hLen + 15.dp.toPx())
                        close()
                    }
                    drawPath(hPath, Color(0xFF0F1A2E).copy(alpha = 0.85f))
                    drawPath(hPath, Color.White, style = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round))
                }

                // Cockpit Hollow Arrow Minute Hand (White skeleton with dark interior)
                rotate(minuteAngle, pivot = center) {
                    val mLen = radius * 0.72f
                    val mPath = Path().apply {
                        moveTo(center.x, center.y - mLen)
                        lineTo(center.x - 7.dp.toPx(), center.y - mLen + 18.dp.toPx())
                        lineTo(center.x - 3.5.dp.toPx(), center.y + 10.dp.toPx())
                        lineTo(center.x + 3.5.dp.toPx(), center.y + 10.dp.toPx())
                        lineTo(center.x + 7.dp.toPx(), center.y - mLen + 18.dp.toPx())
                        close()
                    }
                    drawPath(mPath, Color(0xFF0F1A2E).copy(alpha = 0.85f))
                    drawPath(mPath, Color.White, style = Stroke(2.5.dp.toPx(), cap = StrokeCap.Round))
                }

                // Aviation Sweep Red Chronograph Seconds Hand
                if (showSeconds) {
                    rotate(secondAngle, pivot = center) {
                        val sLen = radius * 0.86f
                        drawLine(
                            color = Color(0xFFEF4444),
                            start = Offset(center.x, center.y + 20.dp.toPx()),
                            end = Offset(center.x, center.y - sLen),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawCircle(Color(0xFFEF4444), 3.5.dp.toPx(), Offset(center.x, center.y - sLen * 0.82f))
                    }
                }

                // Center Pinion Cap
                drawCircle(Color(0xFF0F1A2E), 7.dp.toPx(), center)
                drawCircle(Color.White, 3.dp.toPx(), center)
            }
        }
    }
}
