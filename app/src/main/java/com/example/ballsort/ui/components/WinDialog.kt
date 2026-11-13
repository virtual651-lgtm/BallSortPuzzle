/*
 * com.example.ballsort.ui.components
 * Seviye kazanma ve çıkmaz diyalogları.
 */
package com.example.ballsort.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.ballsort.domain.model.BotDifficulty
import com.example.ballsort.R
import com.example.ballsort.util.formatDuration
import kotlinx.coroutines.delay

@Composable
fun WinDialog(
    levelId: Int,
    isDaily: Boolean,
    stars: Int,
    moves: Int,
    elapsedMs: Long,
    onNextLevel: () -> Unit,
    onReplay: () -> Unit,
    onMenu: () -> Unit,
    isDuel: Boolean = false,
    botDifficulty: BotDifficulty? = null,
) {
    var revealedStars by remember(stars) { mutableIntStateOf(0) }
    LaunchedEffect(stars) {
        revealedStars = 0
        for (i in 1..stars) {
            delay(220L)
            revealedStars = i
        }
    }

    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = stringResource(R.string.win_title)) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = when {
                        isDuel && botDifficulty != null -> "${stringResource(botDifficulty.titleRes)} karşısında kazandın! 🏆"
                        isDuel -> stringResource(R.string.win_duel_subtitle)
                        isDaily -> stringResource(R.string.win_daily_subtitle)
                        else -> stringResource(R.string.win_subtitle, levelId)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 1..3) {
                        val earned = i <= revealedStars
                        val scale by animateFloatAsState(
                            targetValue = if (earned) 1.15f else 0.9f,
                            animationSpec = tween(220),
                            label = "star$i",
                        )
                        Icon(
                            imageVector = if (earned) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = if (earned) Color(0xFFFFC107) else MaterialTheme.colorScheme.outline,
                            modifier = Modifier
                                .size(40.dp)
                                .scale(scale),
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = stringResource(R.string.win_moves, moves))
                Text(text = stringResource(R.string.win_time, formatDuration(elapsedMs)))
            }
        },
        confirmButton = {
            if (isDuel) {
                Button(onClick = onNextLevel) {
                    Text(text = stringResource(R.string.win_new_duel))
                }
            } else if (!isDaily) {
                Button(onClick = onNextLevel) {
                    Text(text = stringResource(R.string.win_next_level))
                }
            } else {
                Button(onClick = onMenu) {
                    Text(text = stringResource(R.string.win_menu))
                }
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = onMenu) {
                    Text(text = stringResource(R.string.win_menu))
                }
                OutlinedButton(onClick = onReplay) {
                    Text(text = stringResource(R.string.win_replay))
                }
            }
        },
    )
}

@Composable
fun StuckDialog(
    canUndo: Boolean,
    extraTubeAvailable: Boolean,
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    onAddTube: () -> Unit,
    isMoveLimitReached: Boolean = false,
    isDuelBotWon: Boolean = false,
) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = stringResource(
                    if (isDuelBotWon) R.string.duel_lost_title else R.string.stuck_title
                )
            )
        },
        text = {
            Text(
                text = stringResource(
                    when {
                        isDuelBotWon -> R.string.duel_lost_message
                        isMoveLimitReached -> R.string.stuck_move_limit_message
                        else -> R.string.stuck_message
                    }
                )
            )
        },
        confirmButton = {
            if (!isDuelBotWon && extraTubeAvailable) {
                Button(onClick = onAddTube) {
                    Text(text = stringResource(R.string.action_add_tube))
                }
            } else {
                Button(onClick = onRestart) {
                    Text(text = stringResource(R.string.action_restart))
                }
            }
        },
        dismissButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (canUndo) {
                    TextButton(onClick = onUndo) {
                        Text(text = stringResource(R.string.action_undo))
                    }
                }
                if (extraTubeAvailable) {
                    OutlinedButton(onClick = onRestart) {
                        Text(text = stringResource(R.string.action_restart))
                    }
                }
            }
        },
    )
}

