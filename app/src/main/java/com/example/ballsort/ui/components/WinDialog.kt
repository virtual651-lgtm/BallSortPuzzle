/*
 * com.example.ballsort.ui.components
 * Seviye kazanma ve çıkmaz diyalogları (Royal Kingdom / Casual 3D tarzı).
 */
package com.example.ballsort.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ballsort.R
import com.example.ballsort.domain.model.BotDifficulty
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
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    var revealedStars by remember(stars) { mutableIntStateOf(0) }

    LaunchedEffect(stars) {
        revealedStars = 0
        delay(150L)
        for (i in 1..stars) {
            delay(260L)
            revealedStars = i
        }
    }

    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
        ) {
            // Ana Kart Gövdesi
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isDark) Color(0xFF1B204A) else Color(0xFFFFFFFF),
                ),
                border = BorderStroke(
                    width = 2.dp,
                    color = if (isDark) Color(0xFF4C579E) else Color(0xFFA5D6A7),
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                ) {
                    Spacer(modifier = Modifier.height(10.dp))

                    // Alt Başlık / Seviye Bilgisi
                    val subtitleText = when {
                        isDuel && botDifficulty != null -> "${stringResource(botDifficulty.titleRes)} Karşısında Zafer! 🏆"
                        isDuel -> stringResource(R.string.win_duel_subtitle)
                        isDaily -> stringResource(R.string.win_daily_subtitle)
                        else -> stringResource(R.string.win_subtitle, levelId)
                    }
                    Text(
                        text = subtitleText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isDark) Color(0xFFE0E3FA) else Color(0xFF2E4E3F),
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3D Kavisli Podyum Yıldızları
                    PodiumStarsRow(
                        revealedStars = revealedStars,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // İstatistik Kapsülleri (Hamle & Süre)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        StatPillCard(
                            label = "HAMLE",
                            value = "$moves",
                            icon = "🎯",
                            isDark = isDark,
                            modifier = Modifier.weight(1f),
                        )
                        StatPillCard(
                            label = "SÜRE",
                            value = formatDuration(elapsedMs),
                            icon = "⏱️",
                            isDark = isDark,
                            modifier = Modifier.weight(1f),
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Birincil Büyük 3D Eylem Butonu (Sonraki Seviye / Yeni Düello)
                    val primaryAction = if (isDuel || !isDaily) onNextLevel else onMenu
                    val primaryLabel = when {
                        isDuel -> stringResource(R.string.win_new_duel)
                        !isDaily -> stringResource(R.string.win_next_level)
                        else -> stringResource(R.string.win_menu)
                    }

                    Button(
                        onClick = primaryAction,
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(Color(0xFF43A047), Color(0xFF2E7D32))
                                ),
                                shape = RoundedCornerShape(22.dp),
                            )
                            .border(1.5.dp, Color(0xFF81C784), RoundedCornerShape(22.dp)),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = primaryLabel,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = 0.5.sp,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // İkincil Butonlar (Tekrar Oyna & Ana Menü)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        OutlinedButton(
                            onClick = onReplay,
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.5.dp, if (isDark) Color(0xFF4C579E) else Color(0xFFB0BEC5)),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.win_replay),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }

                        OutlinedButton(
                            onClick = onMenu,
                            shape = RoundedCornerShape(18.dp),
                            border = BorderStroke(1.5.dp, if (isDark) Color(0xFF4C579E) else Color(0xFFB0BEC5)),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Home,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.win_menu),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }
                }
            }

            // Tepeye Asılı Altın Şerit Banner (✨ TEBRİKLER! ✨)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset(y = 0.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(Color(0xFFFFB300), Color(0xFFFFD54F), Color(0xFFFF8F00))
                        ),
                        shape = RoundedCornerShape(20.dp),
                    )
                    .border(1.5.dp, Color(0xFFFFF9C4), RoundedCornerShape(20.dp))
                    .padding(horizontal = 24.dp, vertical = 8.dp),
            ) {
                Text(
                    text = "✨ ${stringResource(R.string.win_ribbon_title)} ✨",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF4E2C00),
                    letterSpacing = 1.5.sp,
                )
            }
        }
    }
}

@Composable
private fun PodiumStarsRow(
    revealedStars: Int,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .height(96.dp),
    ) {
        // Yıldız 1 (Sol) - Eğik, biraz aşağıda
        PodiumStar(
            index = 1,
            isRevealed = revealedStars >= 1,
            size = 46.dp,
            offsetY = 12.dp,
            rotation = -12f,
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Yıldız 2 (Orta) - BÜYÜK, yüksekte, altın hale ışıması
        PodiumStar(
            index = 2,
            isRevealed = revealedStars >= 2,
            size = 68.dp,
            offsetY = (-6).dp,
            rotation = 0f,
            hasCrownGlow = true,
        )

        Spacer(modifier = Modifier.width(12.dp))

        // Yıldız 3 (Sağ) - Eğik, biraz aşağıda
        PodiumStar(
            index = 3,
            isRevealed = revealedStars >= 3,
            size = 46.dp,
            offsetY = 12.dp,
            rotation = 12f,
        )
    }
}

@Composable
private fun PodiumStar(
    index: Int,
    isRevealed: Boolean,
    size: Dp,
    offsetY: Dp,
    rotation: Float,
    hasCrownGlow: Boolean = false,
) {
    val scale by animateFloatAsState(
        targetValue = if (isRevealed) 1f else 0.8f,
        animationSpec = spring(
            dampingRatio = 0.45f,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "podium_star_$index",
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .offset(y = offsetY)
            .rotate(rotation)
            .scale(scale),
    ) {
        // Ortadaki taç yıldız için altın aura ışıması
        if (isRevealed && hasCrownGlow) {
            Box(
                modifier = Modifier
                    .size(size * 1.35f)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(Color(0x80FFD54F), Color(0x00FFD54F))
                        ),
                        shape = CircleShape,
                    )
            )
        }

        // Yıldız soket taban gölgesi
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = null,
            tint = Color(0x33000000),
            modifier = Modifier
                .size(size)
                .offset(y = 2.dp),
        )

        if (isRevealed) {
            // Canlı Altın Yıldız Tabanı
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(size),
            )
            // 3D Hacimsel Vurgu / Işıltı
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = Color(0xFFFFE082).copy(alpha = 0.5f),
                modifier = Modifier
                    .size(size * 0.82f)
                    .offset(x = (-1).dp, y = (-1).dp),
            )
        } else {
            // Boş soket yıldızı
            Icon(
                imageVector = Icons.Outlined.StarOutline,
                contentDescription = null,
                tint = Color(0xFFB0BEC5).copy(alpha = 0.5f),
                modifier = Modifier.size(size),
            )
        }
    }
}

@Composable
private fun StatPillCard(
    label: String,
    value: String,
    icon: String,
    isDark: Boolean,
    modifier: Modifier = Modifier,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isDark) Color(0xFF222955) else Color(0xFFE8F5E9),
        border = BorderStroke(1.dp, if (isDark) Color(0xFF384382) else Color(0xFFC8E6C9)),
        modifier = modifier,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
        ) {
            Text(
                text = "$icon $label",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFF9FA8DA) else Color(0xFF2E7D32),
                letterSpacing = 1.sp,
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Black,
                color = if (isDark) Color.White else Color(0xFF1B4332),
            )
        }
    }
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
