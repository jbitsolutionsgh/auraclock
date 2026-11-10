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
fun ModernPodsSportsFace(
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

    val sportGreen = Color(0xFF10B981)

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Box(
            modifier = Modifier
                .size(dialSize.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 1. Static authentic modern sports pods dial image
            Image(
                painter = painterResource(id = R.drawable.face_sports_pods),
                contentDescription = "Modern Pods Sports Watch Face",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Overlaid moving hands matching modern sports styling
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f
                val center = this.center

                // Hour Hand: Clean white sports baton with high-contrast shadow
                rotate(hourAngle, pivot = center) {
                    val hLen = radius * 0.50f
                    val hW = 6.dp.toPx()
                    // Shadow
                    drawLine(
                        color = Color.Black.copy(alpha = 0.6f),
                        start = Offset(center.x + 2.dp.toPx(), center.y + 12.dp.toPx()),
                        end = Offset(center.x + 2.dp.toPx(), center.y - hLen + 2.dp.toPx()),
                        strokeWidth = hW,
                        cap = StrokeCap.Round
                    )
                    // White hand
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x, center.y + 10.dp.toPx()),
                        end = Offset(center.x, center.y - hLen),
                        strokeWidth = hW,
                        cap = StrokeCap.Round
                    )
                    // Inner emerald lume
                    drawLine(
                        sportGreen,
                        Offset(center.x, center.y - 4.dp.toPx()),
                        Offset(center.x, center.y - hLen + 6.dp.toPx()),
                        strokeWidth = hW * 0.45f,
                        cap = StrokeCap.Round
                    )
                }

                // Minute Hand: Longer white sports baton
                rotate(minuteAngle, pivot = center) {
                    val mLen = radius * 0.74f
                    val mW = 4.5.dp.toPx()
                    // Shadow
                    drawLine(
                        color = Color.Black.copy(alpha = 0.6f),
                        start = Offset(center.x + 2.dp.toPx(), center.y + 14.dp.toPx()),
                        end = Offset(center.x + 2.dp.toPx(), center.y - mLen + 2.dp.toPx()),
                        strokeWidth = mW,
                        cap = StrokeCap.Round
                    )
                    // White hand
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x, center.y + 12.dp.toPx()),
                        end = Offset(center.x, center.y - mLen),
                        strokeWidth = mW,
                        cap = StrokeCap.Round
                    )
                    // Inner emerald lume
                    drawLine(
                        sportGreen,
                        Offset(center.x, center.y - 6.dp.toPx()),
                        Offset(center.x, center.y - mLen + 8.dp.toPx()),
                        strokeWidth = mW * 0.45f,
                        cap = StrokeCap.Round
                    )
                }

                // Electric Green / Cyan Sweep Seconds Needle
                if (showSeconds) {
                    rotate(secondAngle, pivot = center) {
                        val sLen = radius * 0.88f
                        drawLine(
                            color = sportGreen,
                            start = Offset(center.x, center.y + 18.dp.toPx()),
                            end = Offset(center.x, center.y - sLen),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawCircle(sportGreen, 3.5.dp.toPx(), Offset(center.x, center.y - sLen * 0.82f))
                    }
                }

                // Center Pinion Cap
                drawCircle(Color(0xFF0F172A), 5.5.dp.toPx(), center)
                drawCircle(Color.White, 3.dp.toPx(), center)
            }
        }
    }
}
