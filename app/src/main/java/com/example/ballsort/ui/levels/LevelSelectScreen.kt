/*
 * com.example.ballsort.ui.levels
 * Seviye seçim ızgarası (Royal Pacing, Saray rozetleri, altın sayacı ve kilitler).
 */
package com.example.ballsort.ui.levels

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ballsort.R
import com.example.ballsort.domain.model.LevelTier
import com.example.ballsort.domain.model.PalaceTheme
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LevelSelectScreen(
    onBack: () -> Unit,
    onSelectLevel: (Int) -> Unit,
    viewModel: LevelSelectViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // Boss kart altın ışıma animasyonu
    val bossGlowTransition = rememberInfiniteTransition(label = "bossGlow")
    val bossGlowAlpha by bossGlowTransition.animateFloat(
        initialValue = 0.12f,
        targetValue = 0.32f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bossGlowAlpha",
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                    ) {
                        Text(
                            text = stringResource(R.string.levels_title),
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFB300).copy(alpha = 0.2f),
                            border = BorderStroke(1.dp, Color(0xFFFFB300)),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            ) {
                                Text(text = "🪙", fontSize = 12.sp)
                                Text(
                                    text = "${uiState.coins}",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFFFFB300),
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
            )
        },
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 84.dp),
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            items(uiState.totalLevels) { index ->
                val levelId = index + 1
                val unlocked = levelId <= uiState.unlockedLevel
                val isCurrent = levelId == uiState.unlockedLevel
                val result = uiState.results[levelId]
                val pacing = PalaceTheme.getPacing(levelId)
                val isBoss = pacing.tier == LevelTier.BOSS

                val tierBadge = when (pacing.tier) {
                    LevelTier.NORMAL -> null
                    LevelTier.HARD -> "ZOR" to Color(0xFF8E24AA)
                    LevelTier.SUPER_HARD -> "SÜPER" to Color(0xFFD32F2F)
                    LevelTier.BOSS -> "👑 BOSS" to Color(0xFFFFB300)
                }

                // Kart giriş animasyonu (kısa gecikmeyle skale+fade)
                var visible by remember { mutableStateOf(false) }
                LaunchedEffect(levelId) {
                    delay((index % 20 * 18L).coerceAtMost(260L))
                    visible = true
                }

                // Basılı tutma animasyonu
                val interactionSource = remember { MutableInteractionSource() }
                val isPressed by interactionSource.collectIsPressedAsState()
                val pressScale by animateFloatAsState(
                    targetValue = if (isPressed) 0.93f else 1f,
                    animationSpec = spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow),
                    label = "cardPress_$levelId",
                )

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(220)) + scaleIn(initialScale = 0.82f, animationSpec = spring(dampingRatio = 0.65f)),
                ) {
                    Card(
                        onClick = { if (unlocked) onSelectLevel(levelId) },
                        enabled = unlocked,
                        interactionSource = interactionSource,
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                !unlocked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                                isBoss -> Color(0xFFFFB300).copy(alpha = bossGlowAlpha)
                                isCurrent -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                        ),
                        border = when {
                            isCurrent -> BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                            isBoss && unlocked -> BorderStroke(1.5.dp, Color(0xFFFFB300))
                            else -> null
                        },
                        modifier = Modifier
                            .aspectRatio(0.95f)
                            .scale(pressScale),
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            if (tierBadge != null && unlocked) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .background(tierBadge.second, RoundedCornerShape(bottomStart = 8.dp))
                                        .padding(horizontal = 4.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        text = tierBadge.first,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                    )
                                }
                            }

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(6.dp),
                            ) {
                                Text(
                                    text = levelId.toString(),
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = if (isCurrent || isBoss) FontWeight.Bold else FontWeight.Medium,
                                    ),
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                if (!unlocked) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = stringResource(R.string.levels_locked),
                                        modifier = Modifier.size(18.dp),
                                    )
                                } else if (result != null) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                                        for (s in 1..3) {
                                            Icon(
                                                imageVector = if (s <= result.stars) Icons.Filled.Star else Icons.Outlined.StarOutline,
                                                contentDescription = null,
                                                tint = if (s <= result.stars) Color(0xFFFFC107) else MaterialTheme.colorScheme.outline,
                                                modifier = Modifier.size(16.dp),
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = stringResource(R.string.levels_best_moves, result.bestMoves),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
