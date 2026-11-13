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

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

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
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    Surface(
        color = Color.Transparent,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            // Geri Al (Undo) + Rozet "3"
            CircularGameAction(
                icon = Icons.AutoMirrored.Filled.Undo,
                label = stringResource(R.string.action_undo),
                badgeText = "3",
                enabled = canUndo,
                isDark = isDark,
                onClick = onUndo,
            )

            // İpucu (Hint / Ampul) + Rozet "3"
            CircularGameAction(
                icon = Icons.Default.Lightbulb,
                label = stringResource(R.string.action_hint),
                badgeText = "3",
                enabled = !isSolvingHint,
                loading = isSolvingHint,
                isDark = isDark,
                onClick = onHint,
            )

            // Karıştır / Sıfırla (Shuffle / Restart)
            CircularGameAction(
                icon = Icons.Default.Shuffle,
                label = stringResource(R.string.action_restart),
                badgeText = null,
                enabled = true,
                isDark = isDark,
                onClick = onRestart,
            )

            // Ekstra Tüp (+1)
            if (extraTubeAvailable) {
                CircularGameAction(
                    icon = Icons.Default.AddBox,
                    label = stringResource(R.string.action_add_tube),
                    badgeText = "+1",
                    enabled = true,
                    isDark = isDark,
                    onClick = onAddTube,
                )
            }
        }
    }
}

@Composable
private fun CircularGameAction(
    icon: ImageVector,
    label: String,
    badgeText: String?,
    enabled: Boolean,
    isDark: Boolean,
    loading: Boolean = false,
    onClick: () -> Unit,
) {
    val circleBg = if (isDark) Color(0xFF1D2254) else Color(0xFFC8E6C9)
    val circleBorder = if (isDark) Color(0xFF3B4380) else Color(0xFFA5D6A7)
    val iconColor = if (isDark) Color.White else Color(0xFF1B4332)
    val badgeBg = if (isDark) Color(0xFFFF9800) else Color(0xFF2E7D32)

    Box(contentAlignment = Alignment.BottomEnd) {
        Surface(
            onClick = onClick,
            enabled = enabled,
            shape = CircleShape,
            color = if (enabled) circleBg else circleBg.copy(alpha = 0.45f),
            border = BorderStroke(
                1.5.dp,
                if (enabled) circleBorder else circleBorder.copy(alpha = 0.35f),
            ),
            modifier = Modifier.size(58.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (loading) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        color = iconColor,
                        modifier = Modifier.size(22.dp),
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = label,
                        tint = if (enabled) iconColor else iconColor.copy(alpha = 0.4f),
                        modifier = Modifier.size(26.dp),
                    )
                }
            }
        }

        // Kalan Sayaç Rozeti
        if (badgeText != null && enabled) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(20.dp)
                    .background(badgeBg, CircleShape)
                    .border(1.dp, Color.White.copy(alpha = 0.7f), CircleShape),
            ) {
                Text(
                    text = badgeText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                )
            }
        }
    }
}

