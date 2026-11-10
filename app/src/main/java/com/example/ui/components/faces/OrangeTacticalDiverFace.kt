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
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.ClockTheme
import java.util.Calendar
import kotlin.math.min

@Composable
fun OrangeTacticalDiverFace(
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

    val diverOrange = Color(0xFFFF5500)

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Box(
            modifier = Modifier
                .size(dialSize.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 1. Static authentic tactical orange diver dial image
            Image(
                painter = painterResource(id = R.drawable.face_orange_diver),
                contentDescription = "Tactical Orange Diver Watch Face",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Overlaid moving plongeur diver hands
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f
                val center = this.center

                // Running Seconds on bottom subdial (6 o'clock)
                val botSubCenter = Offset(center.x, center.y + radius * 0.45f)
                val botSubRadius = radius * 0.20f
                rotate(secondAngle, pivot = botSubCenter) {
                    drawLine(
                        color = diverOrange,
                        start = botSubCenter,
                        end = Offset(botSubCenter.x, botSubCenter.y - botSubRadius * 0.80f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Diver Plongeur Hour Hand (broad sword with orange border & black spine)
                rotate(hourAngle, pivot = center) {
                    val hLen = radius * 0.48f
                    val hW = 7.dp.toPx()
                    // Shadow
                    drawLine(
                        color = Color.Black.copy(alpha = 0.6f),
                        start = Offset(center.x + 2.dp.toPx(), center.y + 10.dp.toPx()),
                        end = Offset(center.x + 2.dp.toPx(), center.y - hLen + 2.dp.toPx()),
                        strokeWidth = hW,
                        cap = StrokeCap.Round
                    )
                    // Orange body
                    drawLine(
                        color = diverOrange,
                        start = Offset(center.x, center.y + 8.dp.toPx()),
                        end = Offset(center.x, center.y - hLen),
                        strokeWidth = hW,
                        cap = StrokeCap.Round
                    )
                    // Inner black channel
                    drawLine(
                        color = Color(0xFF1E242B),
                        start = Offset(center.x, center.y - 4.dp.toPx()),
                        end = Offset(center.x, center.y - hLen * 0.65f),
                        strokeWidth = hW * 0.45f,
                        cap = StrokeCap.Round
                    )
                }

                // Diver Plongeur Minute Hand (longer arrow with orange border)
                rotate(minuteAngle, pivot = center) {
                    val mLen = radius * 0.76f
                    val mW = 5.5.dp.toPx()
                    // Shadow
                    drawLine(
                        color = Color.Black.copy(alpha = 0.6f),
                        start = Offset(center.x + 2.dp.toPx(), center.y + 12.dp.toPx()),
                        end = Offset(center.x + 2.dp.toPx(), center.y - mLen + 2.dp.toPx()),
                        strokeWidth = mW,
                        cap = StrokeCap.Round
                    )
                    // Orange body
                    drawLine(
                        color = diverOrange,
                        start = Offset(center.x, center.y + 10.dp.toPx()),
                        end = Offset(center.x, center.y - mLen),
                        strokeWidth = mW,
                        cap = StrokeCap.Round
                    )
                    // Inner black channel
                    drawLine(
                        color = Color(0xFF1E242B),
                        start = Offset(center.x, center.y - 6.dp.toPx()),
                        end = Offset(center.x, center.y - mLen * 0.70f),
                        strokeWidth = mW * 0.45f,
                        cap = StrokeCap.Round
                    )
                }

                // Central Sweep Seconds Hand with luminous dot
                if (showSeconds) {
                    rotate(secondAngle, pivot = center) {
                        val sLen = radius * 0.88f
                        drawLine(
                            color = diverOrange,
                            start = Offset(center.x, center.y + 20.dp.toPx()),
                            end = Offset(center.x, center.y - sLen),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawCircle(diverOrange, 4.dp.toPx(), Offset(center.x, center.y - sLen * 0.75f))
                        drawCircle(Color.White, 2.dp.toPx(), Offset(center.x, center.y - sLen * 0.75f))
                    }
                }

                // Center Pinion Cap
                drawCircle(Color(0xFF0F172A), 7.dp.toPx(), center)
                drawCircle(diverOrange, 3.5.dp.toPx(), center)
            }
        }
    }
}
