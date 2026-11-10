package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ClockTheme
import java.util.Locale
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

data class LapRecord(
    val lapNumber: Int,
    val lapTimeMillis: Long,
    val totalTimeMillis: Long
)

@Composable
fun AnalogStopwatchDial(
    elapsedMillis: Long,
    isRunning: Boolean,
    laps: List<LapRecord>,
    theme: ClockTheme,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onLap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalSeconds = elapsedMillis / 1000
    val minutes = (totalSeconds / 60) % 60
    val seconds = totalSeconds % 60
    val centiseconds = (elapsedMillis % 1000) / 10

    val sweepAngle = (seconds * 6f) + (centiseconds * 0.06f)
    val minutesAngle = (minutes * 12f) + (seconds * 0.2f)
    val hundredthAngle = centiseconds * 3.6f

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dial Area
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            val dialSize = min(maxWidth.value, maxHeight.value) * 0.95f

            Canvas(
                modifier = Modifier
                    .size(dialSize.dp)
                    .aspectRatio(1f)
            ) {
                val radius = size.minDimension / 2f
                val center = this.center

                // Outer Bezel
                drawCircle(
                    color = Color(0xFF0F172A),
                    radius = radius,
                    style = Stroke(width = 12.dp.toPx())
                )
                drawCircle(
                    color = Color(0xFF1E293B),
                    radius = radius - 6.dp.toPx(),
                    style = Stroke(width = 1.dp.toPx())
                )

                // 60-Second Graduation Ticks (300 ticks for 1/5th second resolution)
                val trackRadius = radius - 14.dp.toPx()
                for (i in 0 until 120) {
                    val angle = (i * 3f) * (PI / 180f).toFloat()
                    val isFiveSec = i % 10 == 0
                    val isOneSec = i % 2 == 0
                    val tickLen = when {
                        isFiveSec -> 10.dp.toPx()
                        isOneSec -> 6.dp.toPx()
                        else -> 3.dp.toPx()
                    }
                    val strokeW = if (isFiveSec) 2.5.dp.toPx() else 1.dp.toPx()
                    val col = when {
                        isFiveSec -> theme.primaryColor
                        isOneSec -> Color(0xFFE2E8F0)
                        else -> Color(0xFF475569)
                    }

                    val p1 = Offset(center.x + trackRadius * sin(angle), center.y - trackRadius * cos(angle))
                    val p2 = Offset(center.x + (trackRadius - tickLen) * sin(angle), center.y - (trackRadius - tickLen) * cos(angle))
                    drawLine(col, p1, p2, strokeWidth = strokeW, cap = StrokeCap.Round)
                }

                // Sub-Dial 1: 30-Minute Counter (Top)
                val topCenter = Offset(center.x, center.y - radius * 0.38f)
                val subRadius = radius * 0.22f
                drawCircle(color = Color(0xFF0B132B), radius = subRadius, center = topCenter)
                drawCircle(color = Color(0xFF334155), radius = subRadius, center = topCenter, style = Stroke(1.dp.toPx()))

                for (i in 0 until 30 step 5) {
                    val angle = (i * 12f) * (PI / 180f).toFloat()
                    val p1 = Offset(topCenter.x + subRadius * sin(angle), topCenter.y - subRadius * cos(angle))
                    val p2 = Offset(topCenter.x + (subRadius - 4.dp.toPx()) * sin(angle), topCenter.y - (subRadius - 4.dp.toPx()) * cos(angle))
                    drawLine(Color(0xFF94A3B8), p1, p2, strokeWidth = 1.2.dp.toPx())
                }

                rotate(minutesAngle, topCenter) {
                    drawLine(
                        color = theme.accentColor,
                        start = topCenter,
                        end = Offset(topCenter.x, topCenter.y - subRadius * 0.85f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = topCenter)

                // Sub-Dial 2: 1/100 Second Counter (Bottom)
                val bottomCenter = Offset(center.x, center.y + radius * 0.38f)
                drawCircle(color = Color(0xFF0B132B), radius = subRadius, center = bottomCenter)
                drawCircle(color = Color(0xFF334155), radius = subRadius, center = bottomCenter, style = Stroke(1.dp.toPx()))

                for (i in 0 until 100 step 10) {
                    val angle = (i * 3.6f) * (PI / 180f).toFloat()
                    val p1 = Offset(bottomCenter.x + subRadius * sin(angle), bottomCenter.y - subRadius * cos(angle))
                    val p2 = Offset(bottomCenter.x + (subRadius - 4.dp.toPx()) * sin(angle), bottomCenter.y - (subRadius - 4.dp.toPx()) * cos(angle))
                    drawLine(Color(0xFF94A3B8), p1, p2, strokeWidth = 1.2.dp.toPx())
                }

                rotate(hundredthAngle, bottomCenter) {
                    drawLine(
                        color = theme.primaryColor,
                        start = bottomCenter,
                        end = Offset(bottomCenter.x, bottomCenter.y - subRadius * 0.85f),
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
                drawCircle(color = Color.White, radius = 2.5.dp.toPx(), center = bottomCenter)

                // Main Sweeping Chronograph Hand
                rotate(sweepAngle, center) {
                    val handLen = radius * 0.86f
                    drawLine(
                        color = theme.secondaryColor,
                        start = Offset(center.x, center.y + 24.dp.toPx()),
                        end = Offset(center.x, center.y - handLen),
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                    drawCircle(
                        color = theme.secondaryColor,
                        radius = 4.dp.toPx(),
                        center = Offset(center.x, center.y - radius * 0.58f)
                    )
                }

                drawCircle(color = theme.secondaryColor, radius = 5.dp.toPx(), center = center)
                drawCircle(color = Color.Black, radius = 2.dp.toPx(), center = center)
            }

            // Digital Overlay in middle
            Text(
                text = String.format(Locale.getDefault(), "%02d:%02d.%02d", minutes, seconds, centiseconds),
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = Color.White,
                modifier = Modifier
                    .background(Color(0xCC050A14), RoundedCornerShape(10.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Action Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Reset / Lap Button
            OutlinedButton(
                onClick = {
                    if (isRunning) onLap() else onReset()
                },
                enabled = elapsedMillis > 0,
                modifier = Modifier
                    .size(68.dp)
                    .testTag("stopwatch_lap_reset_button"),
                shape = CircleShape,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = if (isRunning) theme.accentColor else Color(0xFF94A3B8)
                )
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Flag else Icons.Default.Refresh,
                    contentDescription = if (isRunning) "Lap" else "Reset"
                )
            }

            // Start / Pause Button
            Button(
                onClick = {
                    if (isRunning) onPause() else onStart()
                },
                modifier = Modifier
                    .size(76.dp)
                    .testTag("stopwatch_start_pause_button"),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) Color(0xFF7F1D1D) else theme.primaryColor,
                    contentColor = if (isRunning) Color.White else Color.Black
                )
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isRunning) "Pause" else "Start",
                    modifier = Modifier.size(34.dp)
                )
            }
        }

        // Laps List (if any recorded)
        if (laps.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                    .padding(8.dp)
            ) {
                itemsIndexed(laps.reversed()) { _, lap ->
                    val lapSec = lap.lapTimeMillis / 1000
                    val lapMin = (lapSec / 60) % 60
                    val lapS = lapSec % 60
                    val lapCs = (lap.lapTimeMillis % 1000) / 10

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Lap ${lap.lapNumber}",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = String.format(Locale.getDefault(), "%02d:%02d.%02d", lapMin, lapS, lapCs),
                            color = Color.White,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
