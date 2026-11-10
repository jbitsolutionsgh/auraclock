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
fun ClubRetrogradeFace(
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

    val orangeAccent = Color(0xFFFF7A00)

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Box(
            modifier = Modifier
                .size(dialSize.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 1. Authentic High-Res Club Retrograde Base Image
            Image(
                painter = painterResource(id = R.drawable.face_club_retrograde),
                contentDescription = "Club Night Retrograde Watch Face",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Overlaid moving hands matching the aesthetic
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f
                val center = this.center

                // Hour Hand: Brushed steel skeleton with luminous orange tip
                rotate(hourAngle, pivot = center) {
                    val hLen = radius * 0.50f
                    val hW = 7.5.dp.toPx()
                    // Shadow
                    drawLine(
                        color = Color.Black.copy(alpha = 0.5f),
                        start = Offset(center.x + 2.dp.toPx(), center.y + 12.dp.toPx()),
                        end = Offset(center.x + 2.dp.toPx(), center.y - hLen + 2.dp.toPx()),
                        strokeWidth = hW,
                        cap = StrokeCap.Square
                    )
                    // Steel body
                    drawLine(
                        color = Color(0xFF64748B),
                        start = Offset(center.x, center.y + 10.dp.toPx()),
                        end = Offset(center.x, center.y - hLen),
                        strokeWidth = hW,
                        cap = StrokeCap.Square
                    )
                    // Dark interior channel
                    drawLine(
                        color = Color(0xFF1E2228),
                        start = Offset(center.x, center.y - 6.dp.toPx()),
                        end = Offset(center.x, center.y - hLen * 0.65f),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Square
                    )
                    // Orange tip
                    drawLine(
                        orangeAccent,
                        Offset(center.x, center.y - hLen * 0.70f),
                        Offset(center.x, center.y - hLen),
                        strokeWidth = hW,
                        cap = StrokeCap.Square
                    )
                }

                // Minute Hand: Longer steel skeleton with luminous orange tip
                rotate(minuteAngle, pivot = center) {
                    val mLen = radius * 0.74f
                    val mW = 6.dp.toPx()
                    // Shadow
                    drawLine(
                        color = Color.Black.copy(alpha = 0.5f),
                        start = Offset(center.x + 2.dp.toPx(), center.y + 14.dp.toPx()),
                        end = Offset(center.x + 2.dp.toPx(), center.y - mLen + 2.dp.toPx()),
                        strokeWidth = mW,
                        cap = StrokeCap.Square
                    )
                    // Steel body
                    drawLine(
                        color = Color(0xFF64748B),
                        start = Offset(center.x, center.y + 12.dp.toPx()),
                        end = Offset(center.x, center.y - mLen),
                        strokeWidth = mW,
                        cap = StrokeCap.Square
                    )
                    // Dark interior channel
                    drawLine(
                        color = Color(0xFF1E2228),
                        start = Offset(center.x, center.y - 8.dp.toPx()),
                        end = Offset(center.x, center.y - mLen * 0.70f),
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Square
                    )
                    // Orange tip
                    drawLine(
                        orangeAccent,
                        Offset(center.x, center.y - mLen * 0.75f),
                        Offset(center.x, center.y - mLen),
                        strokeWidth = mW,
                        cap = StrokeCap.Square
                    )
                }

                // Fine Orange Sweep Seconds Hand
                if (showSeconds) {
                    rotate(secondAngle, pivot = center) {
                        val sLen = radius * 0.88f
                        drawLine(
                            color = orangeAccent,
                            start = Offset(center.x, center.y + 18.dp.toPx()),
                            end = Offset(center.x, center.y - sLen),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                }

                // Center Pinion
                drawCircle(Color(0xFF0F1216), 6.5.dp.toPx(), center)
                drawCircle(orangeAccent, 3.dp.toPx(), center)
            }
        }
    }
}
