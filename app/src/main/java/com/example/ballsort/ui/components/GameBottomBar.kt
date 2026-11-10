/*
 * com.example.ballsort.ui.components
 * Oyun alt araç çubuğu: Geri Al, Yeniden Başlat, İpucu ve Ekstra Tüp.
 */
package com.example.ballsort.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ballsort.R

@Composable
fun GameBottomBar(
    canUndo: Boolean,
    extraTubeAvailable: Boolean,
    isSolvingHint: Boolean,
    onUndo: () -> Unit,
    onRestart: () -> Unit,
    onHint: () -> Unit,
    onAddTube: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        tonalElevation = 3.dp,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
        ) {
            BottomActionButton(
                icon = Icons.AutoMirrored.Filled.Undo,
                label = stringResource(R.string.action_undo),
                enabled = canUndo,
                onClick = onUndo,
            )
            BottomActionButton(
                icon = Icons.Default.Refresh,
                label = stringResource(R.string.action_restart),
                enabled = true,
                onClick = onRestart,
            )
            BottomActionButton(
                icon = Icons.Default.Lightbulb,
                label = stringResource(R.string.action_hint),
                enabled = !isSolvingHint,
                loading = isSolvingHint,
                onClick = onHint,
            )
            BottomActionButton(
                icon = Icons.Default.AddBox,
                label = stringResource(R.string.action_add_tube),
                enabled = extraTubeAvailable,
                onClick = onAddTube,
            )
        }
    }
}

@Composable
private fun BottomActionButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    loading: Boolean = false,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.defaultMinSize(minWidth = 56.dp, minHeight = 48.dp),
    ) {
        FilledTonalIconButton(
            onClick = onClick,
            enabled = enabled,
            modifier = Modifier.size(48.dp),
        ) {
            if (loading) {
                CircularProgressIndicator(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(20.dp),
                )
            } else {
                Icon(imageVector = icon, contentDescription = label)
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

