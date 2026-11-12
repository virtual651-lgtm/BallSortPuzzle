/*
 * com.example.ballsort.ui.howto
 * Adım adım kaydırılabilir interaktif Walkthrough (Nasıl Oynanır) rehberi.
 */
package com.example.ballsort.ui.howto

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ballsort.R
import com.example.ballsort.ui.components.GameBackdrop
import com.example.ballsort.ui.components.TubeView
import kotlinx.coroutines.launch

private data class WalkthroughPage(
    val titleRes: Int,
    val descRes: Int,
    val preview: @Composable () -> Unit,
)

@Composable
fun HowToPlayScreen(
    onBack: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val pages = listOf(
        WalkthroughPage(
            titleRes = R.string.walkthrough_slide_1_title,
            descRes = R.string.walkthrough_slide_1_desc,
            preview = { GoalPreview() },
        ),
        WalkthroughPage(
            titleRes = R.string.walkthrough_slide_2_title,
            descRes = R.string.walkthrough_slide_2_desc,
            preview = { RulePreview() },
        ),
        WalkthroughPage(
            titleRes = R.string.walkthrough_slide_3_title,
            descRes = R.string.walkthrough_slide_3_desc,
            preview = { BoostersPreview() },
        ),
        WalkthroughPage(
            titleRes = R.string.walkthrough_slide_4_title,
            descRes = R.string.walkthrough_slide_4_desc,
            preview = { DuelPreview() },
        ),
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val isLastPage = pagerState.currentPage == pages.size - 1

    GameBackdrop(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                // Üst Bar: Geri ve Geç Butonları
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                    Text(
                        text = stringResource(R.string.howto_title),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    TextButton(onClick = onBack) {
                        Text(text = stringResource(R.string.walkthrough_skip))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Kaydırılabilir Sayfalar (Horizontal Pager)
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                ) { pageIndex ->
                    val page = pages[pageIndex]
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 4.dp),
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                        ) {
                            // Görsel Önizleme Alanı
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                            ) {
                                page.preview()
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Metin Alanı
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                Text(
                                    text = stringResource(page.titleRes),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    text = stringResource(page.descRes),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 8.dp),
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Alt Kısım: Nokta Göstergeleri (Page Indicator) ve İleri/Başla Butonu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Sayfa Göstergesi
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        repeat(pages.size) { index ->
                            val isSelected = pagerState.currentPage == index
                            Box(
                                modifier = Modifier
                                    .size(if (isSelected) 12.dp else 8.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary
                                        else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                    ),
                            )
                        }
                    }

                    // İleri / Başla Butonu
                    Button(
                        onClick = {
                            if (isLastPage) {
                                onBack()
                            } else {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                    ) {
                        Text(
                            text = if (isLastPage) stringResource(R.string.walkthrough_start)
                            else stringResource(R.string.walkthrough_next),
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
        }
    }
}

// 1. Slayt Görseli: Hedef (Tek renk tamamlanmış tüpler)
@Composable
private fun GoalPreview() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DemoTube(tubes = listOf(0, 0, 0, 0)) // Kırmızı tüp
        DemoTube(tubes = listOf(1, 1, 1, 1)) // Mavi tüp
        DemoTube(tubes = emptyList())         // Boş tüp
    }
}

// 2. Slayt Görseli: Kural (Aynı renk üzerine aktarım)
@Composable
private fun RulePreview() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Kaynak tüp (Mavi top seçili)
        DemoTube(tubes = listOf(0, 2, 1), isSelected = true)
        // Hedef tüp (En üstünde mavi var, izin verilir)
        DemoTube(tubes = listOf(3, 1))
    }
}

// 3. Slayt Görseli: Güçlendiriciler / Yardımcılar
@Composable
private fun BoostersPreview() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BoosterCard(icon = Icons.AutoMirrored.Filled.Undo, label = stringResource(R.string.action_undo), color = Color(0xFF2196F3))
        BoosterCard(icon = Icons.Default.Lightbulb, label = stringResource(R.string.action_hint), color = Color(0xFFFFC107))
        BoosterCard(icon = Icons.Default.AddCircle, label = stringResource(R.string.action_add_tube), color = Color(0xFF4CAF50))
    }
}

@Composable
private fun BoosterCard(icon: ImageVector, label: String, color: Color) {
    Surface(
        tonalElevation = 3.dp,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.size(88.dp),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(8.dp),
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
        }
    }
}

// 4. Slayt Görseli: Wi-Fi ve Canlı Düello
@Composable
private fun DuelPreview() {
    Surface(
        tonalElevation = 3.dp,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.padding(12.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(20.dp),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.Wifi,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(44.dp),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Wi-Fi", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
            Text(text = "VS", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = Color(0xFFE91E63))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(44.dp),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "QR Kod", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DemoTube(
    tubes: List<Int>,
    isSelected: Boolean = false,
) {
    TubeView(
        index = 0,
        tube = tubes,
        capacity = 4,
        tubeWidth = 46.dp,
        ballSize = 28.dp,
        isSelected = isSelected,
        isHintSource = false,
        isHintTarget = false,
        shakeToken = 0L,
        hiddenSlots = IntRange.EMPTY,
        colorBlind = false,
        onSlotMeasured = { _, _ -> },
        onTopExitMeasured = {},
        onClick = {},
    )
}
