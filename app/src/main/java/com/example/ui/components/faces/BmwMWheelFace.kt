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
fun BmwMWheelFace(
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

    val mBlue = Color(0xFF0099FF)
    val mRed = Color(0xFFFF1A1A)

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Box(
            modifier = Modifier
                .size(dialSize.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 1. Static authentic BMW M Wheel dial image
            Image(
                painter = painterResource(id = R.drawable.face_bmw_wheel),
                contentDescription = "BMW M Wheel Watch Face",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Overlaid live hands matching the alloy spoke design
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f
                val center = this.center

                // Hour Hand: Silver baton with electric blue inner stripe
                rotate(hourAngle, pivot = center) {
                    val hLen = radius * 0.52f
                    val hW = 6.dp.toPx()
                    // Shadow
                    drawLine(
                        color = Color.Black.copy(alpha = 0.6f),
                        start = Offset(center.x + 2.dp.toPx(), center.y + 12.dp.toPx()),
                        end = Offset(center.x + 2.dp.toPx(), center.y - hLen + 2.dp.toPx()),
                        strokeWidth = hW,
                        cap = StrokeCap.Round
                    )
                    // Polished steel
                    drawLine(
                        color = Color(0xFFE2E8F0),
                        start = Offset(center.x, center.y + 10.dp.toPx()),
                        end = Offset(center.x, center.y - hLen),
                        strokeWidth = hW,
                        cap = StrokeCap.Round
                    )
                    // M-Blue stripe
                    drawLine(
                        color = mBlue,
                        start = Offset(center.x, center.y - 4.dp.toPx()),
                        end = Offset(center.x, center.y - hLen + 6.dp.toPx()),
                        strokeWidth = hW * 0.45f,
                        cap = StrokeCap.Round
                    )
                }

                // Minute Hand: Longer silver baton with electric blue inner stripe
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
                    // Polished steel
                    drawLine(
                        color = Color(0xFFE2E8F0),
                        start = Offset(center.x, center.y + 12.dp.toPx()),
                        end = Offset(center.x, center.y - mLen),
                        strokeWidth = mW,
                        cap = StrokeCap.Round
                    )
                    // M-Blue stripe
                    drawLine(
                        color = mBlue,
                        start = Offset(center.x, center.y - 6.dp.toPx()),
                        end = Offset(center.x, center.y - mLen + 8.dp.toPx()),
                        strokeWidth = mW * 0.45f,
                        cap = StrokeCap.Round
                    )
                }

                // Seconds Hand: M-Red racing needle
                if (showSeconds) {
                    rotate(secondAngle, pivot = center) {
                        val sLen = radius * 0.88f
                        drawLine(
                            color = mRed,
                            start = Offset(center.x, center.y + 18.dp.toPx()),
                            end = Offset(center.x, center.y - sLen),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawCircle(mBlue, 3.5.dp.toPx(), Offset(center.x, center.y - sLen * 0.82f))
                    }
                }

                // Center Pinion: Silver Hub
                drawCircle(Color(0xFF0F172A), 5.5.dp.toPx(), center)
                drawCircle(Color(0xFFE2E8F0), 3.dp.toPx(), center)
            }
        }
    }
}
