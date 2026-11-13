/*
 * com.example.ballsort.ui.components
 * Oyun üst çubuğu: geri butonu, seviye başlığı, süre ve hamle sayacı.
 */
package com.example.ballsort.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.ballsort.R
import com.example.ballsort.util.formatDuration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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
    botMoves: Int = 0,
    botTotalMoves: Int = 0,
    isLanDuel: Boolean = false,
    lanOpponentCompleted: Int = 0,
    lanOpponentTotal: Int = 7,
    lanOpponentMoves: Int = 0,
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val circleBg = if (isDark) Color(0xFF1E2352) else Color(0xFFC8E6C9)
    val circleBorder = BorderStroke(1.5.dp, if (isDark) Color(0xFF3B4380) else Color(0xFFA5D6A7))
    val primaryText = if (isDark) Color.White else Color(0xFF1B4332)

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

                // Orta: Seviye Başlığı, İnce Çizgi ve 3 Hedef Yıldız
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                ) {
                    Text(
                        text = when {
                            isDuel -> stringResource(R.string.duel_level_title)
                            isDaily -> stringResource(R.string.daily_level_title)
                            else -> stringResource(R.string.level_title, levelId)
                        },
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                        ),
                        color = primaryText,
                    )

                    // İnce Seviye Çizgisi
                    Box(
                        modifier = Modifier
                            .width(64.dp)
                            .height(3.dp)
                            .background(
                                color = if (isDark) Color(0xFF4F8DFF).copy(alpha = 0.6f) else Color(0xFF81C784),
                                shape = RoundedCornerShape(2.dp),
                            )
                    )

                    // 3 Hedef Yıldız
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        repeat(3) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFC107).copy(alpha = 0.85f),
                                modifier = Modifier.size(18.dp),
                            )
                        }
                    }
                }

                // Sağ: Süre ve Hamle Sayaç Kapsülü
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = circleBg,
                    border = circleBorder,
                    modifier = Modifier.height(44.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    ) {
                        Text(
                            text = formatDuration(elapsedMs),
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = primaryText,
                        )
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(14.dp)
                                .background(primaryText.copy(alpha = 0.25f))
                        )
                        Text(
                            text = "${moves}",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (maxMoves != null && (maxMoves - moves) <= 3) MaterialTheme.colorScheme.error else primaryText,
                        )
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
                ) {
                    Text(
                        text = stringResource(R.string.duel_bot_progress, botMoves, botTotalMoves),
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
            }
        }
    }
}

