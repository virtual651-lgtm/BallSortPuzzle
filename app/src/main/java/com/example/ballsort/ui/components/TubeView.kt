/*
 * com.example.ballsort.ui.components
 * Cam efektli tüp bileşeni: seçim kaldırma, geçersiz hamle sallanması, ipucu vurgusu ve TalkBack.
 */
package com.example.ballsort.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.example.ballsort.R
import com.example.ballsort.domain.GameLogic
import com.example.ballsort.domain.model.Tube
import com.example.ballsort.ui.theme.BallSkin
import com.example.ballsort.ui.theme.TubeSkin
import com.example.ballsort.ui.theme.ballEntryFor
import kotlin.math.roundToInt

@Composable
fun TubeView(
    index: Int,
    tube: Tube,
    capacity: Int,
    tubeWidth: Dp,
    ballSize: Dp,
    isSelected: Boolean,
    isHintSource: Boolean,
    isHintTarget: Boolean,
    shakeToken: Long,
    hiddenSlots: IntRange,
    colorBlind: Boolean,
    onSlotMeasured: (slotIndex: Int, rootCenter: Offset) -> Unit,
    onTopExitMeasured: (rootCenter: Offset) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    ballSkin: String = BallSkin.CLASSIC.id,
    tubeSkin: String = TubeSkin.GLASS.id,
    isLocked: Boolean = false,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val isCompleted = GameLogic.isTubeCompleted(tube, capacity)

    // Geçersiz hamle sallanma animasyonu
    val shakeOffset = remember { Animatable(0f) }
    LaunchedEffect(shakeToken) {
        if (shakeToken > 0L) {
            shakeOffset.snapTo(0f)
            shakeOffset.animateTo(
                targetValue = 0f,
                animationSpec = keyframes {
                    durationMillis = 250
                    -14f at 40
                    14f at 90
                    -10f at 140
                    10f at 190
                    0f at 250
                },
            )
        }
    }

    // Tamamlanan tüpte hafif büyüme nabzı
    val completeScale = remember { Animatable(1f) }
    LaunchedEffect(isCompleted) {
        if (isCompleted) {
            completeScale.animateTo(1.06f, tween(180))
            completeScale.animateTo(1f, tween(280))
        }
    }

    // İpucu nabzı
    val infiniteTransition = rememberInfiniteTransition(label = "hintPulse")
    val hintPulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isHintSource || isHintTarget) 1.07f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(550),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "hintScale",
    )

    // Seçilen tüpün en üst topu yukarı kalkar
    val topLiftDp by animateDpAsState(
        targetValue = if (isSelected) 16.dp else 0.dp,
        animationSpec = tween(150),
        label = "topLift",
    )

    val resolvedTubeSkin = TubeSkin.fromId(tubeSkin)
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val defaultBorderColor = when (resolvedTubeSkin) {
        TubeSkin.GLASS -> if (isDark) Color.White.copy(alpha = 0.55f) else Color(0xFF2E7D32).copy(alpha = 0.40f)
        TubeSkin.DARK -> Color(0xFF607D8B).copy(alpha = 0.75f)
        TubeSkin.WOOD -> Color(0xFF8D6E63)
        TubeSkin.GOLD -> Color(0xFFD4AF37)
    }

    val borderColor by animateColorAsState(
        targetValue = when {
            isSelected -> MaterialTheme.colorScheme.primary
            isHintSource -> Color(0xFFFFC107)
            isHintTarget -> Color(0xFF00E676)
            isCompleted -> Color(0xFF4CAF50)
            isLocked -> Color(0xFFFF9800).copy(alpha = 0.75f)
            else -> defaultBorderColor
        },
        animationSpec = tween(200),
        label = "tubeBorder",
    )

    val defaultBgColor = when (resolvedTubeSkin) {
        TubeSkin.GLASS -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.36f)
        TubeSkin.DARK -> Color(0xFF1C2331).copy(alpha = 0.65f)
        TubeSkin.WOOD -> Color(0xFF795548).copy(alpha = 0.22f)
        TubeSkin.GOLD -> Color(0xFFFFD54F).copy(alpha = 0.16f)
    }

    val tubeBgColor = when {
        isCompleted -> Color(0xFF4CAF50).copy(alpha = 0.14f)
        isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        else -> defaultBgColor
    }

    // Erişilebilirlik metinleri
    val a11yDesc = if (tube.isEmpty()) {
        stringResource(R.string.a11y_tube_empty, index + 1)
    } else {
        val colorNames = tube.joinToString(", ") { colorId ->
            context.getString(ballEntryFor(colorId).nameRes)
        }
        stringResource(R.string.a11y_tube_contents, index + 1, colorNames)
    }

    val stateDesc = when {
        isSelected -> stringResource(R.string.a11y_tube_selected)
        isHintSource -> stringResource(R.string.a11y_tube_hint_source)
        isHintTarget -> stringResource(R.string.a11y_tube_hint_target)
        else -> null
    }

    val spacing = 4.dp
    val tubeInnerHeight = (ballSize * capacity) + (spacing * (capacity + 1)) + 8.dp
    val tubeShape = RoundedCornerShape(
        topStart = 8.dp,
        topEnd = 8.dp,
        bottomStart = tubeWidth / 2f,
        bottomEnd = tubeWidth / 2f,
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .defaultMinSize(minWidth = minOf(48.dp, tubeWidth), minHeight = 48.dp)
            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
            .scale(completeScale.value * hintPulse)
            .semantics(mergeDescendants = true) {
                contentDescription = a11yDesc
                if (stateDesc != null) stateDescription = stateDesc
            }
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Button,
                onClick = onClick,
            )
            .padding(horizontal = 2.dp),
    ) {
        // Seçili topun yükselmesi, uçuş çıkış noktası ve tamamlanan tüp mührü için üst alan
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .width(tubeWidth)
                .height(22.dp)
                .onGloballyPositioned { coords ->
                    val pos = coords.positionInRoot()
                    onTopExitMeasured(
                        Offset(
                            x = pos.x + coords.size.width / 2f,
                            y = pos.y + coords.size.height / 2f,
                        )
                    )
                },
        ) {
            androidx.compose.animation.AnimatedVisibility(
                visible = isCompleted,
                enter = fadeIn(tween(220)) + scaleIn(initialScale = 0.7f, animationSpec = tween(220)),
                exit = fadeOut(tween(150)) + scaleOut(targetScale = 0.7f, animationSpec = tween(150)),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .width(tubeWidth * 0.68f)
                        .height(14.dp)
                        .background(
                            color = Color(0xFF4CAF50),
                            shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 2.dp, bottomEnd = 2.dp),
                        ),
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(11.dp),
                    )
                }
            }
            if (isLocked && !isCompleted) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(20.dp)
                        .background(Color(0xFFFF9800), CircleShape),
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = stringResource(R.string.tube_locked_badge),
                        tint = Color.White,
                        modifier = Modifier.size(12.dp),
                    )
                }
            }
        }

        // Tüp gövdesi
        Box(
            contentAlignment = Alignment.BottomCenter,
            modifier = Modifier
                .width(tubeWidth)
                .height(tubeInnerHeight)
                .background(color = tubeBgColor, shape = tubeShape)
                .border(
                    width = if (isSelected || isHintSource || isHintTarget || isCompleted) 2.5.dp else 1.5.dp,
                    color = borderColor,
                    shape = tubeShape,
                ),
        ) {
            // Üst cam ağızlık (rim) parlaması
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                borderColor.copy(alpha = 0.35f),
                                Color.White.copy(alpha = 0.45f),
                                borderColor.copy(alpha = 0.35f),
                            ),
                        ),
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                    ),
            )

            // Sol kenar dikey cam yansıması
            Box(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 5.dp, top = 12.dp, bottom = 16.dp)
                    .width(3.dp)
                    .fillMaxHeight()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.55f),
                                Color.White.copy(alpha = 0.18f),
                                Color.Transparent,
                            ),
                        ),
                        shape = CircleShape,
                    ),
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(spacing, Alignment.Bottom),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 6.dp, top = 4.dp),
            ) {
                // Yukarıdan aşağıya slot indexleri: (capacity - 1) downTo 0
                for (slot in (capacity - 1) downTo 0) {
                    val ballColor = tube.getOrNull(slot)
                    val isTopBall = slot == tube.lastIndex && ballColor != null
                    val liftPx = if (isTopBall) with(density) { -topLiftDp.toPx() } else 0f
                    val isHiddenForFlight = slot in hiddenSlots

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .width(ballSize)
                            .height(ballSize)
                            .onGloballyPositioned { coords ->
                                val pos = coords.positionInRoot()
                                onSlotMeasured(
                                    slot,
                                    Offset(
                                        x = pos.x + coords.size.width / 2f,
                                        y = pos.y + coords.size.height / 2f,
                                    ),
                                )
                            },
                    ) {
                        if (ballColor != null) {
                            BallView(
                                colorId = ballColor,
                                ballSize = ballSize,
                                colorBlind = colorBlind,
                                ballSkin = ballSkin,
                                modifier = Modifier
                                    .offset { IntOffset(0, liftPx.roundToInt()) }
                                    .alpha(if (isHiddenForFlight) 0f else 1f),
                            )
                        } else {
                            Spacer(modifier = Modifier.height(ballSize))
                        }
                    }
                }
            }
        }
    }
}

