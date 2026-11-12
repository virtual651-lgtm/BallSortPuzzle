/*
 * com.example.ballsort.ui.components
 * Topların tüpler arası yay (arc) animasyonunu üst katmanda çizer (satırlar arası kırpılmaz).
 */
package com.example.ballsort.ui.components

import android.animation.ValueAnimator
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import com.example.ballsort.ui.game.TransferAnimation
import com.example.ballsort.ui.theme.BallSkin
import com.example.ballsort.ui.theme.ballEntryFor
import kotlin.math.min

@Composable
fun FlyingBallOverlay(
    transfer: TransferAnimation?,
    ballSize: Dp,
    colorBlind: Boolean,
    rootTopLeft: Offset,
    topExitPoints: Map<Int, Offset>,
    slotPoints: Map<Pair<Int, Int>, Offset>,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier,
    ballSkin: String = BallSkin.CLASSIC.id,
) {
    if (transfer == null) return

    val density = LocalDensity.current
    val radiusPx = with(density) { ballSize.toPx() / 2f }
    val progress = remember(transfer.id) { Animatable(0f) }
    var activeBallOffset by remember(transfer.id) { mutableStateOf<Offset?>(null) }

    val startPoint = topExitPoints[transfer.from]
    val endPoint = slotPoints[transfer.to to transfer.targetStartSlot]
    val targetExit = topExitPoints[transfer.to]

    LaunchedEffect(transfer.id) {
        val animatorsEnabled = runCatching { ValueAnimator.areAnimatorsEnabled() }.getOrDefault(true)
        if (!animatorsEnabled || startPoint == null || endPoint == null || targetExit == null) {
            onFinished()
            return@LaunchedEffect
        }
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        )
        onFinished()
    }

    if (startPoint == null || endPoint == null || targetExit == null) return

    val t = progress.value
    val p0 = startPoint - rootTopLeft
    val p1 = targetExit - rootTopLeft
    val p2 = endPoint - rootTopLeft

    // 2 aşamalı yol: (0..0.65) üstten yay çizerek hedef tüpün ağzına, (0.65..1.0) hedef slota iniş
    val currentCenter = if (t < 0.65f) {
        val u = (t / 0.65f).coerceIn(0f, 1f)
        val peakY = min(p0.y, p1.y) - radiusPx * 2.2f
        val control = Offset((p0.x + p1.x) / 2f, peakY)
        quadBezier(p0, control, p1, u)
    } else {
        val u = ((t - 0.65f) / 0.35f).coerceIn(0f, 1f)
        Offset(
            x = p1.x + (p2.x - p1.x) * u,
            y = p1.y + (p2.y - p1.y) * u,
        )
    }
    activeBallOffset = currentCenter

    val entry = ballEntryFor(transfer.colorId)
    Canvas(modifier = modifier.fillMaxSize()) {
        val base = activeBallOffset ?: return@Canvas
        // Çoklu taşımada ardışık topları hafif dikey aralıkla çiz
        for (i in 0 until transfer.count) {
            val offsetCenter = Offset(base.x, base.y - i * radiusPx * 2.05f)
            drawBall(
                color = entry.color,
                symbol = entry.symbol,
                colorBlind = colorBlind,
                center = offsetCenter,
                radius = radiusPx,
                ballSkin = ballSkin,
            )
        }
    }
}

private fun quadBezier(p0: Offset, p1: Offset, p2: Offset, t: Float): Offset {
    val inv = 1f - t
    return Offset(
        x = inv * inv * p0.x + 2f * inv * t * p1.x + t * t * p2.x,
        y = inv * inv * p0.y + 2f * inv * t * p1.y + t * t * p2.y,
    )
}

