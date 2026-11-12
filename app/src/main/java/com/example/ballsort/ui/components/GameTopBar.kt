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
    Surface(
        color = Color.Transparent,
        modifier = modifier.fillMaxWidth(),
    ) {
        val pillColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.72f)
        val pillBorder = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = pillColor,
                    border = pillBorder,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 14.dp),
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.back),
                            )
                        }
                        Text(
                            text = when {
                                isDuel -> stringResource(R.string.duel_level_title)
                                isDaily -> stringResource(R.string.daily_level_title)
                                else -> stringResource(R.string.level_title, levelId)
                            },
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = pillColor,
                    border = pillBorder,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    ) {
                        Text(
                            text = formatDuration(elapsedMs),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        val movesText = if (maxMoves != null) {
                            stringResource(R.string.moves_limit_label, moves, maxMoves)
                        } else {
                            stringResource(R.string.moves_label, moves)
                        }
                        val nearLimit = maxMoves != null && (maxMoves - moves) <= 5
                        Text(
                            text = movesText,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (nearLimit) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
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

