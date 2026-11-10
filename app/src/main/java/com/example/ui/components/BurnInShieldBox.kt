package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.random.Random

@Composable
fun BurnInShieldBox(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    dimLevel: Float = 1.0f,
    glowColor: Color = Color.Transparent,
    content: @Composable BoxScope.() -> Unit
) {
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }

    LaunchedEffect(enabled) {
        if (!enabled) {
            offsetX.snapTo(0f)
            offsetY.snapTo(0f)
            return@LaunchedEffect
        }

        while (true) {
            delay(45000L) // Shift every 45 seconds to preserve OLED subpixels
            val targetX = (Random.nextInt(-16, 17)).toFloat()
            val targetY = (Random.nextInt(-16, 17)).toFloat()

            // Smooth 3-second easing so user hardly notices the micro-shift
            offsetX.animateTo(targetX, tween(3000, easing = FastOutSlowInEasing))
            offsetY.animateTo(targetY, tween(3000, easing = FastOutSlowInEasing))
        }
    }

    Box(
        modifier = modifier
            .alpha(dimLevel.coerceIn(0.1f, 1.0f))
            .drawBehind {
                if (glowColor != Color.Transparent) {
                    val radius = size.minDimension * 0.75f
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(glowColor, Color.Transparent),
                            center = center,
                            radius = radius
                        ),
                        radius = radius
                    )
                }
            }
            .offset {
                IntOffset(
                    x = offsetX.value.dp.roundToPx(),
                    y = offsetY.value.dp.roundToPx()
                )
            },
        contentAlignment = Alignment.Center,
        content = content
    )
}
