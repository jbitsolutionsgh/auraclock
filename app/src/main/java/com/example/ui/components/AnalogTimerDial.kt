package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnalogTimerDial(
    totalDurationSeconds: Long,
    remainingSeconds: Long,
    isRunning: Boolean,
    theme: ClockTheme,
    onStart: () -> Unit,
    onPause: () -> Unit,
    onReset: () -> Unit,
    onAddSeconds: (Long) -> Unit,
    onSetPreset: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val progress = if (totalDurationSeconds > 0) {
        remainingSeconds.toFloat() / totalDurationSeconds.toFloat()
    } else 0f

    val hours = remainingSeconds / 3600
    val minutes = (remainingSeconds % 3600) / 60
    val seconds = remainingSeconds % 60

    val timeString = if (hours > 0) {
        String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Preset Chips
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val presets = listOf(
                "1m" to 60L,
                "3m" to 180L,
                "5m" to 300L,
                "10m" to 600L,
                "15m" to 900L,
                "25m Pomodoro" to 1500L,
                "45m" to 2700L
            )
            for ((label, secs) in presets) {
                FilterChip(
                    selected = totalDurationSeconds == secs,
                    onClick = { onSetPreset(secs) },
                    label = { Text(label, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = theme.primaryColor,
                        selectedLabelColor = Color.Black,
                        containerColor = Color(0xFF1E293B),
                        labelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Center: Circular Analog Dial
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            val dialSize = min(maxWidth.value, maxHeight.value) * 0.92f

            Canvas(
                modifier = Modifier
                    .size(dialSize.dp)
                    .aspectRatio(1f)
            ) {
                val radius = size.minDimension / 2f
                val strokeW = 12.dp.toPx()
                val center = this.center

                // Outer bezel
                drawCircle(
                    color = Color(0xFF0F172A),
                    radius = radius,
                    style = Stroke(width = strokeW)
                )

                // 60-Second Graduation Ticks around perimeter
                val trackRadius = radius - strokeW - 4.dp.toPx()
                for (i in 0 until 60) {
                    val angle = (i * 6f) * (PI / 180f).toFloat()
                    val isFiveMin = i % 5 == 0
                    val tickLen = if (isFiveMin) 8.dp.toPx() else 4.dp.toPx()
                    val col = if (isFiveMin) theme.accentColor else Color(0xFF334155)

                    val p1 = Offset(center.x + trackRadius * sin(angle), center.y - trackRadius * cos(angle))
                    val p2 = Offset(center.x + (trackRadius - tickLen) * sin(angle), center.y - (trackRadius - tickLen) * cos(angle))
                    drawLine(col, p1, p2, strokeWidth = if (isFiveMin) 2.dp.toPx() else 1.dp.toPx())
                }

                // Remaining Time Arc (Sweeping clockwise)
                val sweepAngle = progress * 360f
                val arcRadius = radius - strokeW / 2f
                drawArc(
                    color = theme.primaryColor,
                    startAngle = -90f,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = Offset(center.x - arcRadius, center.y - arcRadius),
                    size = Size(arcRadius * 2, arcRadius * 2),
                    style = Stroke(width = strokeW * 0.9f, cap = StrokeCap.Round)
                )

                // Glowing Tip Indicator at tip of progress arc
                val tipAngleRad = (-90f + sweepAngle) * (PI / 180f).toFloat()
                val tipX = center.x + arcRadius * cos(tipAngleRad)
                val tipY = center.y + arcRadius * sin(tipAngleRad)

                drawCircle(color = theme.primaryColor.copy(alpha = 0.4f), radius = strokeW, center = Offset(tipX, tipY))
                drawCircle(color = Color.White, radius = strokeW * 0.4f, center = Offset(tipX, tipY))
            }

            // Digital Time Remaining in Center
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = timeString,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color.White,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = if (isRunning) "COUNTING DOWN" else "PAUSED",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isRunning) theme.accentColor else Color(0xFF94A3B8),
                    letterSpacing = 2.sp
                )
            }
        }

        // Granular increment adjustment chips (+15s, +1m, +5m, -1m)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onAddSeconds(-60) },
                modifier = Modifier
                    .background(Color(0xFF1E293B), CircleShape)
                    .size(40.dp)
            ) {
                Text("-1m", color = Color(0xFF94A3B8), fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = { onAddSeconds(15) },
                modifier = Modifier
                    .background(Color(0xFF1E293B), CircleShape)
                    .size(40.dp)
            ) {
                Text("+15s", color = theme.accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = { onAddSeconds(60) },
                modifier = Modifier
                    .background(Color(0xFF1E293B), CircleShape)
                    .size(40.dp)
            ) {
                Text("+1m", color = theme.accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(12.dp))
            IconButton(
                onClick = { onAddSeconds(300) },
                modifier = Modifier
                    .background(Color(0xFF1E293B), CircleShape)
                    .size(40.dp)
            ) {
                Text("+5m", color = theme.accentColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Bottom Controls (Reset, Start/Pause)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onReset,
                modifier = Modifier
                    .size(68.dp)
                    .testTag("timer_reset_button"),
                shape = CircleShape,
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF94A3B8))
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset")
            }

            Button(
                onClick = {
                    if (isRunning) onPause() else onStart()
                },
                enabled = remainingSeconds > 0,
                modifier = Modifier
                    .size(76.dp)
                    .testTag("timer_start_pause_button"),
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
    }
}
