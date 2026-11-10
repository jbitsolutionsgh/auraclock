package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun CustomGradientPicker(
    initialColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    var hueFraction by remember { mutableFloatStateOf(0.5f) } // 0..1
    var brightnessFraction by remember { mutableFloatStateOf(0.95f) } // 0.2..1

    val hueGradientColors = remember {
        listOf(
            Color(0xFFFF0000), // Red
            Color(0xFFFF8800), // Orange
            Color(0xFFFFFF00), // Yellow
            Color(0xFF00FF00), // Green
            Color(0xFF00FFFF), // Cyan
            Color(0xFF0066FF), // Blue
            Color(0xFF9900FF), // Violet
            Color(0xFFFF0088), // Pink
            Color(0xFFFF0000)  // Red
        )
    }

    // Calculate color from hue and brightness
    val activeColor = remember(hueFraction, brightnessFraction) {
        val hsv = FloatArray(3)
        hsv[0] = (hueFraction * 360f).coerceIn(0f, 360f)
        hsv[1] = 0.95f // high saturation for vibrant horology glow
        hsv[2] = brightnessFraction.coerceIn(0.2f, 1.0f)
        Color(android.graphics.Color.HSVToColor(hsv))
    }

    val hexString = remember(activeColor) {
        String.format("#%06X", (0xFFFFFF and activeColor.toArgb()))
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0B101D), RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Color Preview & Hex indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(activeColor, CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = hexString,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { onColorSelected(activeColor) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = activeColor,
                    contentColor = if (brightnessFraction > 0.6f && (hueFraction in 0.12f..0.45f)) Color.Black else Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("apply_custom_color_button")
            ) {
                Text("Apply", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        // 1. Hue Gradient Spectrum Slider
        Text(
            text = "HUE SPECTRUM",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            letterSpacing = 1.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(34.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(34.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            hueFraction = (offset.x / size.width).coerceIn(0f, 1f)
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            hueFraction = (change.position.x / size.width).coerceIn(0f, 1f)
                        }
                    }
            ) {
                // Gradient track
                drawRoundRect(
                    brush = Brush.horizontalGradient(hueGradientColors),
                    size = Size(size.width, size.height),
                    cornerRadius = CornerRadius(17.dp.toPx())
                )

                // Draggable indicator thumb
                val thumbX = (hueFraction * size.width).coerceIn(17.dp.toPx(), size.width - 17.dp.toPx())
                drawCircle(
                    color = Color.White,
                    radius = 16.dp.toPx(),
                    center = Offset(thumbX, size.height / 2f)
                )
                drawCircle(
                    color = activeColor,
                    radius = 12.dp.toPx(),
                    center = Offset(thumbX, size.height / 2f)
                )
            }
        }

        // 2. Luminance / Brightness Gradient Slider
        Text(
            text = "LUMINANCE",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            letterSpacing = 1.sp
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            brightnessFraction = (offset.x / size.width).coerceIn(0.2f, 1f)
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            change.consume()
                            brightnessFraction = (change.position.x / size.width).coerceIn(0.2f, 1f)
                        }
                    }
            ) {
                val brightGradient = Brush.horizontalGradient(
                    listOf(Color(0xFF222222), activeColor)
                )
                drawRoundRect(
                    brush = brightGradient,
                    size = Size(size.width, size.height),
                    cornerRadius = CornerRadius(14.dp.toPx())
                )

                val thumbX = (brightnessFraction * size.width).coerceIn(14.dp.toPx(), size.width - 14.dp.toPx())
                drawCircle(
                    color = Color.White,
                    radius = 13.dp.toPx(),
                    center = Offset(thumbX, size.height / 2f)
                )
            }
        }
    }
}
