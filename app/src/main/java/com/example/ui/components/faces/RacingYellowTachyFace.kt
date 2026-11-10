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
fun RacingYellowTachyFace(
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

    val yellowColor = Color(0xFFFFD700)

    BoxWithConstraints(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        val dialSize = min(maxWidth.value, maxHeight.value) * 0.94f

        Box(
            modifier = Modifier
                .size(dialSize.dp)
                .clip(CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // 1. Static authentic high-res dial image
            Image(
                painter = painterResource(id = R.drawable.face_racing_yellow),
                contentDescription = "Tachymeter Pro Yellow Watch Face",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // 2. Overlaid moving hands & needles matching the original aesthetic
            Canvas(modifier = Modifier.fillMaxSize()) {
                val radius = size.minDimension / 2f
                val center = this.center

                // Top "PHONE" Battery Needle (Sub-dial located at top center)
                val topSubCenter = Offset(center.x, center.y - radius * 0.40f)
                val topSubRadius = radius * 0.15f
                // Battery needle points around 1/2 to Full (around 25 degrees)
                val batteryAngle = 20f + ((minute % 10) * 4f)
                rotate(batteryAngle, pivot = topSubCenter) {
                    drawLine(
                        color = Color.White,
                        start = topSubCenter,
                        end = Offset(topSubCenter.x, topSubCenter.y - topSubRadius * 0.85f),
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Bottom "WATCH" Gauge Needle (Sub-dial at bottom center)
                val botSubCenter = Offset(center.x, center.y + radius * 0.40f)
                val botSubRadius = radius * 0.15f
                val watchAngle = -15f + ((minute % 15) * 2f)
                rotate(watchAngle, pivot = botSubCenter) {
                    drawLine(
                        color = Color.White,
                        start = botSubCenter,
                        end = Offset(botSubCenter.x, botSubCenter.y - botSubRadius * 0.85f),
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }

                // Hour Hand: Chunky black industrial skeleton hand with yellow tip
                rotate(hourAngle, pivot = center) {
                    val hLen = radius * 0.50f
                    val hW = 8.dp.toPx()
                    // Drop shadow
                    drawLine(
                        color = Color.Black.copy(alpha = 0.5f),
                        start = Offset(center.x + 2.dp.toPx(), center.y + 12.dp.toPx()),
                        end = Offset(center.x + 2.dp.toPx(), center.y - hLen + 2.dp.toPx()),
                        strokeWidth = hW,
                        cap = StrokeCap.Square
                    )
                    // Black skeleton frame
                    drawLine(
                        color = Color(0xFF1E2633),
                        start = Offset(center.x, center.y + 10.dp.toPx()),
                        end = Offset(center.x, center.y - hLen),
                        strokeWidth = hW,
                        cap = StrokeCap.Square
                    )
                    // Yellow tip
                    drawLine(
                        color = yellowColor,
                        start = Offset(center.x, center.y - hLen * 0.65f),
                        end = Offset(center.x, center.y - hLen + 2.dp.toPx()),
                        strokeWidth = hW * 0.55f,
                        cap = StrokeCap.Square
                    )
                    // Luminous white inner block
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x, center.y - hLen * 0.78f),
                        end = Offset(center.x, center.y - hLen + 3.dp.toPx()),
                        strokeWidth = hW * 0.4f,
                        cap = StrokeCap.Square
                    )
                }

                // Minute Hand: Longer skeleton hand with matching yellow & white tip
                rotate(minuteAngle, pivot = center) {
                    val mLen = radius * 0.72f
                    val mW = 6.5.dp.toPx()
                    // Drop shadow
                    drawLine(
                        color = Color.Black.copy(alpha = 0.5f),
                        start = Offset(center.x + 2.dp.toPx(), center.y + 14.dp.toPx()),
                        end = Offset(center.x + 2.dp.toPx(), center.y - mLen + 2.dp.toPx()),
                        strokeWidth = mW,
                        cap = StrokeCap.Square
                    )
                    // Black skeleton frame
                    drawLine(
                        color = Color(0xFF1E2633),
                        start = Offset(center.x, center.y + 12.dp.toPx()),
                        end = Offset(center.x, center.y - mLen),
                        strokeWidth = mW,
                        cap = StrokeCap.Square
                    )
                    // Yellow tip
                    drawLine(
                        color = yellowColor,
                        start = Offset(center.x, center.y - mLen * 0.70f),
                        end = Offset(center.x, center.y - mLen + 2.dp.toPx()),
                        strokeWidth = mW * 0.55f,
                        cap = StrokeCap.Square
                    )
                    // Luminous white inner block
                    drawLine(
                        color = Color.White,
                        start = Offset(center.x, center.y - mLen * 0.82f),
                        end = Offset(center.x, center.y - mLen + 3.dp.toPx()),
                        strokeWidth = mW * 0.4f,
                        cap = StrokeCap.Square
                    )
                }

                // Sweeping Yellow Seconds Needle with Triangular Arrow Tip
                if (showSeconds) {
                    rotate(secondAngle, pivot = center) {
                        val sLen = radius * 0.86f
                        drawLine(
                            color = yellowColor,
                            start = Offset(center.x, center.y + 20.dp.toPx()),
                            end = Offset(center.x, center.y - sLen),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        // Arrowhead
                        val arrowPath = Path().apply {
                            moveTo(center.x, center.y - sLen)
                            lineTo(center.x - 4.5.dp.toPx(), center.y - sLen + 9.dp.toPx())
                            lineTo(center.x + 4.5.dp.toPx(), center.y - sLen + 9.dp.toPx())
                            close()
                        }
                        drawPath(arrowPath, yellowColor)
                    }
                }

                // Center Pinion Cap
                drawCircle(Color(0xFF0F172A), 6.dp.toPx(), center)
                drawCircle(yellowColor, 3.dp.toPx(), center)
                drawCircle(Color.Black, 1.5.dp.toPx(), center)
            }
        }
    }
}
