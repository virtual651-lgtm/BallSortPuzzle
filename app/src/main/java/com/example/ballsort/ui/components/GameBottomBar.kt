/*
 * com.example.ballsort.ui.components
 * Oyun alt araç çubuğu: Geri Al, Yeniden Başlat, İpucu ve Ekstra Tüp.
 */
package com.example.ballsort.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        color = Color.Transparent,
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
        Surface(
            shape = CircleShape,
            color = Color.Transparent,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = if (enabled) 0.14f else 0.05f)),
        ) {
            FilledTonalIconButton(
                onClick = onClick,
                enabled = enabled,
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.78f),
                ),
                modifier = Modifier.size(50.dp),
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
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

