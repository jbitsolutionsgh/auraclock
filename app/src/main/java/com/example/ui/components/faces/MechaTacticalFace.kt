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
fun MechaTacticalFace(
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

    val mechaOrange = Color(0xFFFF7A00)

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Box(
            modifier = Modifier
                .size(dialSize.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 1. Authentic High-Res Mecha Tactical Base Image
            Image(
                painter = painterResource(id = R.drawable.face_mecha_tactical),
                contentDescription = "Mecha Tactical Watch Face",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Overlaid moving hands matching armor mecha aesthetic
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f
                val center = this.center

                // Futuristic Mecha-Claw Hour Hand
                rotate(hourAngle, pivot = center) {
                    val hLen = radius * 0.50f
                    val hPath = Path().apply {
                        moveTo(center.x, center.y - hLen)
                        lineTo(center.x - 6.dp.toPx(), center.y - hLen + 14.dp.toPx())
                        lineTo(center.x - 2.dp.toPx(), center.y + 8.dp.toPx())
                        lineTo(center.x + 2.dp.toPx(), center.y + 8.dp.toPx())
                        lineTo(center.x + 6.dp.toPx(), center.y - hLen + 14.dp.toPx())
                        close()
                    }
                    drawPath(hPath, Color(0xFF1E232B))
                    drawPath(hPath, Color(0xFF4C566A), style = Stroke(1.5.dp.toPx()))

                    val tipPath = Path().apply {
                        moveTo(center.x, center.y - hLen)
                        lineTo(center.x - 6.dp.toPx(), center.y - hLen + 14.dp.toPx())
                        lineTo(center.x + 6.dp.toPx(), center.y - hLen + 14.dp.toPx())
                        close()
                    }
                    drawPath(tipPath, mechaOrange)
                }

                // Futuristic Mecha-Claw Minute Hand
                rotate(minuteAngle, pivot = center) {
                    val mLen = radius * 0.74f
                    val mPath = Path().apply {
                        moveTo(center.x, center.y - mLen)
                        lineTo(center.x - 5.dp.toPx(), center.y - mLen + 18.dp.toPx())
                        lineTo(center.x - 2.dp.toPx(), center.y + 10.dp.toPx())
                        lineTo(center.x + 2.dp.toPx(), center.y + 10.dp.toPx())
                        lineTo(center.x + 5.dp.toPx(), center.y - mLen + 18.dp.toPx())
                        close()
                    }
                    drawPath(mPath, Color(0xFF1E232B))
                    drawPath(mPath, Color(0xFF4C566A), style = Stroke(1.5.dp.toPx()))

                    val mTipPath = Path().apply {
                        moveTo(center.x, center.y - mLen)
                        lineTo(center.x - 5.dp.toPx(), center.y - mLen + 18.dp.toPx())
                        lineTo(center.x + 5.dp.toPx(), center.y - mLen + 18.dp.toPx())
                        close()
                    }
                    drawPath(mTipPath, mechaOrange)
                }

                // Seconds needle with mecha counterweight
                if (showSeconds) {
                    rotate(secondAngle, pivot = center) {
                        val sLen = radius * 0.86f
                        drawLine(
                            color = mechaOrange,
                            start = Offset(center.x, center.y + 16.dp.toPx()),
                            end = Offset(center.x, center.y - sLen),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawCircle(Color(0xFF1E232B), 4.dp.toPx(), Offset(center.x, center.y + 10.dp.toPx()))
                        drawCircle(mechaOrange, 2.dp.toPx(), Offset(center.x, center.y + 10.dp.toPx()))
                    }
                }

                // Center Pinion Cap
                drawCircle(Color(0xFF0F1115), 7.dp.toPx(), center)
                drawCircle(mechaOrange, 3.5.dp.toPx(), center)
            }
        }
    }
}
