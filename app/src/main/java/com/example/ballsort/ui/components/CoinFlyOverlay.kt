/*
 * com.example.ballsort.ui.components
 * Kazanılan altınların ekranın ortasından üst bardaki altın sayacına uçma kutlama animasyonu.
 */
package com.example.ballsort.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

@Composable
fun CoinFlyOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier,
) {
    if (!visible) return

    val density = LocalDensity.current
    val coinCount = 7
    val animatables = remember(visible) { List(coinCount) { Animatable(0f) } }

    LaunchedEffect(visible) {
        animatables.forEachIndexed { index, anim ->
            launch {
                delay(index * 90L)
                anim.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing),
                )
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val startCenter = Offset(size.width / 2f, size.height * 0.55f)
        val targetPoint = Offset(size.width - 45.dp.toPx(), 45.dp.toPx())
        val radiusBase = 12.dp.toPx()

        animatables.forEachIndexed { i, anim ->
            val p = anim.value
            if (p in 0.01f..0.99f) {
                // Her altın için hafif farklı kavis
                val spreadX = (i - 3) * 28.dp.toPx()
                val peakControl = Offset(
                    x = (startCenter.x + targetPoint.x) / 2f + spreadX,
                    y = (startCenter.y + targetPoint.y) / 2f - 60.dp.toPx() - (i % 2) * 20.dp.toPx(),
                )

                // 2. derece Bezier eğrisi
                val inv = 1f - p
                val currentX = inv * inv * startCenter.x + 2f * inv * p * peakControl.x + p * p * targetPoint.x
                val currentY = inv * inv * startCenter.y + 2f * inv * p * peakControl.y + p * p * targetPoint.y

                val scale = if (p < 0.2f) p / 0.2f else if (p > 0.85f) 1f - (p - 0.85f) / 0.15f * 0.4f else 1f
                val currentRadius = radiusBase * scale
                val alpha = if (p > 0.85f) 1f - (p - 0.85f) / 0.15f else 1f

                val center = Offset(currentX, currentY)

                // Altın parıltısı ve gövdesi
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFFF9C4).copy(alpha = alpha),
                            Color(0xFFFFD54F).copy(alpha = alpha),
                            Color(0xFFFF8F00).copy(alpha = alpha),
                        ),
                        center = Offset(center.x - currentRadius * 0.3f, center.y - currentRadius * 0.3f),
                        radius = currentRadius * 1.3f,
                    ),
                    radius = currentRadius,
                    center = center,
                )

                // Altın kenarlık çizgisi
                drawCircle(
                    color = Color(0xFFFFE082).copy(alpha = alpha),
                    radius = currentRadius * 0.72f,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.5.dp.toPx()),
                )
            }
        }
    }
}
