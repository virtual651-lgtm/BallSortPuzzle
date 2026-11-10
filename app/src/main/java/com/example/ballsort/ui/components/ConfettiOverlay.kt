/*
 * com.example.ballsort.ui.components
 * Seviye kazanıldığında Canvas üzerinde konfeti parçacık animasyonu gösterir.
 */
package com.example.ballsort.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.ballsort.ui.theme.BallPalette
import kotlin.random.Random

private data class ConfettiParticle(
    val startXFraction: Float,
    val speedY: Float,
    val driftX: Float,
    val sizePx: Float,
    val rotationSpeed: Float,
    val color: Color,
)

@Composable
fun ConfettiOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!visible) return

    val particles = remember {
        val rnd = Random(42)
        List(56) {
            ConfettiParticle(
                startXFraction = rnd.nextFloat(),
                speedY = 0.65f + rnd.nextFloat() * 0.75f,
                driftX = (rnd.nextFloat() - 0.5f) * 140f,
                sizePx = 10f + rnd.nextFloat() * 12f,
                rotationSpeed = (rnd.nextFloat() - 0.5f) * 540f,
                color = BallPalette[rnd.nextInt(BallPalette.size)].color,
            )
        }
    }

    val progress = remember { Animatable(0f) }
    LaunchedEffect(visible) {
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1_600, easing = LinearEasing),
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val p = progress.value
        val alpha = (1f - p * 0.7f).coerceIn(0f, 1f)
        particles.forEach { item ->
            val x = item.startXFraction * size.width + item.driftX * p
            val y = -40f + (size.height + 120f) * p * item.speedY
            if (y <= size.height) {
                rotate(degrees = item.rotationSpeed * p, pivot = Offset(x, y)) {
                    drawRect(
                        color = item.color.copy(alpha = alpha),
                        topLeft = Offset(x - item.sizePx / 2f, y - item.sizePx / 2f),
                        size = Size(item.sizePx, item.sizePx * 0.65f),
                    )
                }
            }
        }
    }
}

