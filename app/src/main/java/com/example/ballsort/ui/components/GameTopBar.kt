/*
 * com.example.ballsort.ui.components
 * Oyun üst çubuğu: geri butonu, saray aşaması, seviye rozeti (Zor/Süper Zor/Boss),
 * altın sayacı, süre ve hamle sayaçları.
 */
package com.example.ballsort.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ballsort.R
import com.example.ballsort.domain.model.BotDifficulty
import com.example.ballsort.domain.model.LevelPacing
import com.example.ballsort.domain.model.LevelTier
import com.example.ballsort.util.formatDuration

@Composable
fun GameTopBar(
    levelId: Int,
    isDaily: Boolean,
    moves: Int,
    elapsedMs: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    maxMoves: Int? = null,
    isDuel: Boolean = false,
    botDifficulty: BotDifficulty? = null,
    botMoves: Int = 0,
    botTotalMoves: Int = 0,
    isLanDuel: Boolean = false,
    lanOpponentCompleted: Int = 0,
    lanOpponentTotal: Int = 7,
    lanOpponentMoves: Int = 0,
    coins: Int = 0,
    pacing: LevelPacing? = null,
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val circleBg = if (isDark) Color(0xFF1E2352) else Color(0xFFC8E6C9)
    val circleBorder = BorderStroke(1.5.dp, if (isDark) Color(0xFF3B4380) else Color(0xFFA5D6A7))
    val primaryText = if (isDark) Color.White else Color(0xFF1B4332)

    val tier = pacing?.tier ?: LevelTier.NORMAL
    val tierColor = when (tier) {
        LevelTier.NORMAL -> Color.Transparent
        LevelTier.HARD -> Color(0xFF8E24AA) // 🟣 Mor
        LevelTier.SUPER_HARD -> Color(0xFFD32F2F) // 🔴 Kırmızı
        LevelTier.BOSS -> Color(0xFFFFB300) // 👑 Altın
    }

    val tierLabel = when (tier) {
        LevelTier.NORMAL -> null
        LevelTier.HARD -> "ZOR"
        LevelTier.SUPER_HARD -> "SÜPER ZOR"
        LevelTier.BOSS -> pacing?.bossTitle?.uppercase() ?: "BOSS DÜELLOSU"
    }

    Surface(
        color = Color.Transparent,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                // Sol: Dairesel Geri Butonu
                Surface(
                    onClick = onBackClick,
                    shape = CircleShape,
                    color = circleBg,
                    border = circleBorder,
                    modifier = Modifier.size(46.dp),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            tint = primaryText,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                }

                // Orta: Saray Adı, Seviye Başlığı ve Pacing Rozeti
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    if (pacing != null && !isDaily && !isLanDuel) {
                        Text(
                            text = pacing.eraName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp,
                            ),
                            color = primaryText.copy(alpha = 0.75f),
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = when {
                                isLanDuel -> stringResource(R.string.menu_duel_lan_short)
                                pacing?.tier == LevelTier.BOSS -> pacing?.bossTitle ?: stringResource(R.string.duel_level_title)
                                isDuel -> stringResource(R.string.duel_level_title)
                                isDaily -> stringResource(R.string.daily_level_title)
                                else -> stringResource(R.string.level_title, levelId)
                            },
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                            ),
                            color = primaryText,
                        )

                        if (tierLabel != null) {
                            Box(
                                modifier = Modifier
                                    .background(tierColor, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = tierLabel,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }

                    // 3 Hedef Yıldız
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        repeat(3) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107).copy(alpha = 0.9f),
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }

                // Sağ: Altın Kapsülü + Hamle/Süre Sayaçları
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    // Altın Kapsülü
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFFFC107).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(0xFFFFC107)),
                        modifier = Modifier.height(26.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp),
                        ) {
                            Text(text = "🪙", fontSize = 12.sp)
                            Text(
                                text = "$coins",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = primaryText,
                            )
                        }
                    }

                    // Hamle Sayaç Kapsülü (Zaman kısıtlaması yok, saf bulmaca keyfi)
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = circleBg,
                        border = circleBorder,
                        modifier = Modifier.height(30.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        ) {
                            Text(text = "🎯", fontSize = 11.sp)
                            Text(
                                text = "$moves",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = if (maxMoves != null && (maxMoves - moves) <= 3) MaterialTheme.colorScheme.error else primaryText,
                            )
                        }
                    }
                }
            }

            if (isLanDuel && lanOpponentTotal > 0) {
                val progress = (lanOpponentCompleted.toFloat() / lanOpponentTotal.toFloat()).coerceIn(0f, 1f)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.duel_lan_progress, lanOpponentCompleted, lanOpponentTotal, lanOpponentMoves),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    LinearProgressIndicator(
                        progress = { progress },
                        color = MaterialTheme.colorScheme.error,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                }
            } else if (isDuel && botTotalMoves > 0) {
                val progress = (botMoves.toFloat() / botTotalMoves.toFloat()).coerceIn(0f, 1f)
                val opponentName = pacing?.bossTitle ?: botDifficulty?.let { stringResource(it.titleRes) } ?: "Bot"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                ) {
                    Text(
                        text = "$opponentName: $botMoves/$botTotalMoves",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.error,
                    )
                    LinearProgressIndicator(
                        progress = { progress },
                        color = MaterialTheme.colorScheme.error,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}
