/*
 * com.example.ballsort.ui.components
 * Topların tüpler arası yay (arc) animasyonunu üst katmanda çizer (satırlar arası kırpılmaz).
 */
package com.example.ballsort.ui.components

import android.animation.ValueAnimator
import android.os.Build
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
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import com.example.ballsort.ui.game.SwapAnimation
import com.example.ballsort.ui.game.TransferAnimation
import com.example.ballsort.ui.theme.BallSkin
import com.example.ballsort.ui.theme.ballEntryFor
import kotlin.math.PI
import kotlin.math.min
import kotlin.math.sin

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
    swapTransfer: SwapAnimation? = null,
) {
    if (transfer == null && swapTransfer == null) return

    val density = LocalDensity.current
    val radiusPx = with(density) { ballSize.toPx() / 2f }
    val animId = transfer?.id ?: swapTransfer?.id ?: 0L
    val progress = remember(animId) { Animatable(0f) }

    val isSwap = swapTransfer != null
    val animDuration = if (isSwap) 380 else 320

    LaunchedEffect(animId) {
        val animatorsEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            runCatching { ValueAnimator.areAnimatorsEnabled() }.getOrDefault(true)
        } else {
            true
        }
        if (!animatorsEnabled) {
            onFinished()
            return@LaunchedEffect
        }
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = animDuration, easing = FastOutSlowInEasing),
        )
        onFinished()
    }

    val t = progress.value

    if (transfer != null) {
        val startPoint = topExitPoints[transfer.from]
        val endPoint = slotPoints[transfer.to to transfer.targetStartSlot]
        val targetExit = topExitPoints[transfer.to]

        if (startPoint == null || endPoint == null || targetExit == null) {
            return
        }

        val p0 = startPoint - rootTopLeft
        val p1 = targetExit - rootTopLeft
        val p2 = endPoint - rootTopLeft

        // 2 aşamalı kavis: (0..0.65) üstten yay çizerek hedef tüpe, (0.65..1.0) slota iniş + esneme
        val currentCenter: Offset
        val squashX: Float
        val squashY: Float

        if (t < 0.65f) {
            val u = (t / 0.65f).coerceIn(0f, 1f)
            val peakY = min(p0.y, p1.y) - radiusPx * 2.3f
            val control = Offset((p0.x + p1.x) / 2f, peakY)
            currentCenter = quadBezier(p0, control, p1, u)
            squashX = 1f
            squashY = 1f
        } else {
            val u = ((t - 0.65f) / 0.35f).coerceIn(0f, 1f)
            // Yere çarpma yaylanması (Bounce)
            val bounceY = if (u > 0.75f) {
                val bounceU = (u - 0.75f) / 0.25f
                -sin(bounceU * PI.toFloat()) * (radiusPx * 0.28f)
            } else {
                0f
            }
            currentCenter = Offset(
                x = p1.x + (p2.x - p1.x) * u,
                y = p1.y + (p2.y - p1.y) * u + bounceY,
            )
            // Yere çarpma esnemesi (Squash & Stretch)
            if (u > 0.75f) {
                val factor = sin(((u - 0.75f) / 0.25f) * PI.toFloat())
                squashX = 1f + 0.12f * factor
                squashY = 1f - 0.12f * factor
            } else {
                squashX = 1f
                squashY = 1f
            }
        }

        val entry = ballEntryFor(transfer.colorId)
        Canvas(modifier = modifier.fillMaxSize()) {
            withTransform({
                scale(squashX, squashY, pivot = currentCenter)
            }) {
                for (i in 0 until transfer.count) {
                    val offsetCenter = Offset(currentCenter.x, currentCenter.y - i * radiusPx * 2.05f)
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
    } else if (swapTransfer != null) {
        // Çapraz Top Takası (Swap) Animasyonu
        val startPointA = topExitPoints[swapTransfer.tubeA]
        val endPointA = slotPoints[swapTransfer.tubeB to swapTransfer.targetSlotB]
        val exitB = topExitPoints[swapTransfer.tubeB]

        val startPointB = topExitPoints[swapTransfer.tubeB]
        val endPointB = slotPoints[swapTransfer.tubeA to swapTransfer.targetSlotA]
        val exitA = topExitPoints[swapTransfer.tubeA]

        if (startPointA == null || endPointA == null || exitB == null ||
            startPointB == null || endPointB == null || exitA == null
        ) {
            return
        }

        val p0A = startPointA - rootTopLeft
        val p1A = exitB - rootTopLeft
        val p2A = endPointA - rootTopLeft

        val p0B = startPointB - rootTopLeft
        val p1B = exitA - rootTopLeft
        val p2B = endPointB - rootTopLeft

        val centerA: Offset
        val centerB: Offset
        val squashA: Float
        val squashB: Float

        if (t < 0.65f) {
            val u = (t / 0.65f).coerceIn(0f, 1f)
            // A topu normal yay çizer, B topu biraz daha yüksek yay çizer (havada çarpışmazlar)
            val peakYA = min(p0A.y, p1A.y) - radiusPx * 2.1f
            val controlA = Offset((p0A.x + p1A.x) / 2f, peakYA)
            centerA = quadBezier(p0A, controlA, p1A, u)

            val peakYB = min(p0B.y, p1B.y) - radiusPx * 3.2f
            val controlB = Offset((p0B.x + p1B.x) / 2f, peakYB)
            centerB = quadBezier(p0B, controlB, p1B, u)
            squashA = 1f
            squashB = 1f
        } else {
            val u = ((t - 0.65f) / 0.35f).coerceIn(0f, 1f)
            val bounceY = if (u > 0.75f) {
                -sin(((u - 0.75f) / 0.25f) * PI.toFloat()) * (radiusPx * 0.28f)
            } else 0f

            centerA = Offset(p1A.x + (p2A.x - p1A.x) * u, p1A.y + (p2A.y - p1A.y) * u + bounceY)
            centerB = Offset(p1B.x + (p2B.x - p1B.x) * u, p1B.y + (p2B.y - p1B.y) * u + bounceY)

            val factor = if (u > 0.75f) sin(((u - 0.75f) / 0.25f) * PI.toFloat()) else 0f
            squashA = 1f + 0.12f * factor
            squashB = 1f + 0.12f * factor
        }

        val entryA = ballEntryFor(swapTransfer.colorA)
        val entryB = ballEntryFor(swapTransfer.colorB)

        Canvas(modifier = modifier.fillMaxSize()) {
            withTransform({
                scale(squashA, 2f - squashA, pivot = centerA)
            }) {
                drawBall(
                    color = entryA.color,
                    symbol = entryA.symbol,
                    colorBlind = colorBlind,
                    center = centerA,
                    radius = radiusPx,
                    ballSkin = ballSkin,
                )
            }

            withTransform({
                scale(squashB, 2f - squashB, pivot = centerB)
            }) {
                drawBall(
                    color = entryB.color,
                    symbol = entryB.symbol,
                    colorBlind = colorBlind,
                    center = centerB,
                    radius = radiusPx,
                    ballSkin = ballSkin,
                )
            }
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

