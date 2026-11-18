/*
 * com.example.ballsort.ui.components
 * Oyun alt araç çubuğu: Geri Al, Yeniden Başlat, İpucu ve Ekstra Tüp.
 * Dinamik haklar, altın bedeli ve jeton rozetleriyle donatılmıştır.
 */
package com.example.ballsort.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ballsort.R

@Composable
fun GameBottomBar(
    canUndo: Boolean,
    freeUndosRemaining: Int = 3,
    undoTokens: Int = 0,
    coins: Int = 0,
    extraTubeAvailable: Boolean = true,
    extraTubeTokens: Int = 0,
    swapTokens: Int = 0,
    isSwapMode: Boolean = false,
    isSolvingHint: Boolean = false,
    onUndo: () -> Unit,
    onSwap: () -> Unit,
    onHint: () -> Unit,
    onAddTube: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    val undoBadge = when {
        freeUndosRemaining > 0 -> "$freeUndosRemaining"
        undoTokens > 0 -> "${undoTokens}x"
        coins >= 20 -> "20🪙"
        else -> "0"
    }

    val swapBadge = when {
        isSwapMode -> "✕"
        swapTokens > 0 -> "${swapTokens}x"
        coins >= 50 -> "50🪙"
        else -> "50🪙"
    }

    val tubeBadge = when {
        extraTubeTokens > 0 -> "${extraTubeTokens}x"
        coins >= 80 -> "80🪙"
        else -> "80🪙"
    }

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
            // Geri Al (Undo) + Dinamik Hak/Altın Rozeti
            CircularGameAction(
                icon = Icons.AutoMirrored.Filled.Undo,
                label = stringResource(R.string.action_undo),
                badgeText = undoBadge,
                enabled = canUndo && !isSwapMode,
                isDark = isDark,
                onClick = onUndo,
            )

            // Top Takası (Swap Booster)
            CircularGameAction(
                icon = Icons.Default.SwapHoriz,
                label = stringResource(R.string.action_swap),
                badgeText = swapBadge,
                enabled = true,
                isDark = isDark,
                isHighlighted = isSwapMode,
                onClick = onSwap,
            )

            // İpucu (Hint / Ampul)
            CircularGameAction(
                icon = Icons.Default.Lightbulb,
                label = stringResource(R.string.action_hint),
                badgeText = "💡",
                enabled = !isSolvingHint && !isSwapMode,
                loading = isSolvingHint,
                isDark = isDark,
                onClick = onHint,
            )

            // Ekstra Tüp (+1)
            if (extraTubeAvailable) {
                CircularGameAction(
                    icon = Icons.Default.AddBox,
                    label = stringResource(R.string.action_add_tube),
                    badgeText = tubeBadge,
                    enabled = !isSwapMode,
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    isHighlighted: Boolean = false,
) {
    val buttonBg = when {
        isHighlighted -> if (isDark) Color(0xFFFFB300) else Color(0xFFFFC107)
        isDark -> Color(0xFF1E2352)
        else -> Color(0xFFC8E6C9)
    }
    val buttonBorder = when {
        isHighlighted -> BorderStroke(2.dp, Color(0xFFFFD54F))
        isDark -> BorderStroke(1.5.dp, Color(0xFF3B4380))
        else -> BorderStroke(1.5.dp, Color(0xFFA5D6A7))
    }
    val iconTint = when {
        isHighlighted -> Color.Black
        isDark -> Color.White
        else -> Color(0xFF1B4332)
    }
    val labelColor = when {
        isHighlighted -> if (isDark) Color(0xFFFFD54F) else Color(0xFFE65100)
        isDark -> Color(0xFFE0E0E0)
        else -> Color(0xFF2E7D32)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Surface(
                onClick = onClick,
                enabled = enabled && !loading,
                shape = CircleShape,
                color = buttonBg,
                border = buttonBorder,
                shadowElevation = if (enabled) 4.dp else 0.dp,
                modifier = Modifier.size(54.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp,
                            color = iconTint,
                        )
                    } else {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = if (enabled) iconTint else iconTint.copy(alpha = 0.38f),
                            modifier = Modifier.size(26.dp),
                        )
                    }
                }
            }

            // Rozet (Badge)
            if (badgeText != null) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .defaultMinSize(minWidth = 18.dp, minHeight = 18.dp)
                        .background(
                            color = if (badgeText.contains("🪙")) Color(0xFFFFB300) else Color(0xFF8E24AA),
                            shape = RoundedCornerShape(9.dp),
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(9.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp),
                ) {
                    Text(
                        text = badgeText,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
            ),
            color = if (enabled) labelColor else labelColor.copy(alpha = 0.45f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
