/*
 * com.example.ballsort.ui.menu
 * Ana menü ekranı.
 */
package com.example.ballsort.ui.menu

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Canvas
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Palette
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ballsort.ui.components.GameBackdrop
import com.example.ballsort.ui.theme.BallPalette
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import com.example.ballsort.domain.model.BotDifficulty
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ballsort.R
import com.example.ballsort.platform.LanDuelManager
import com.example.ballsort.platform.LanDuelState
import com.example.ballsort.platform.UpdateDownloadState
import com.example.ballsort.ui.components.QrCodeHelper
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

@Composable
fun MenuScreen(
    onPlayLevel: (Int) -> Unit,
    onOpenLevels: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenHowTo: () -> Unit,
    viewModel: MenuViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val lanState by viewModel.lanDuelState.collectAsStateWithLifecycle()
    val downloadState by viewModel.downloadState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showAchievements by remember { mutableStateOf(false) }
    var showBotDifficultyDialog by remember { mutableStateOf(false) }
    var showLanLobby by remember { mutableStateOf(false) }
    var dismissedUpdateVersion by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(lanState) {
        if (showLanLobby && lanState is LanDuelState.InMatch) {
            showLanLobby = false
            onPlayLevel(-3)
        }
    }

    LaunchedEffect(uiState.shouldShowWalkthrough) {
        if (uiState.shouldShowWalkthrough) {
            viewModel.dismissInitialWalkthrough()
            onOpenHowTo()
        }
    }

    GameBackdrop(modifier = Modifier.fillMaxSize()) {
        val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 12.dp),
            ) {
                // 1. ÜST BAR (Sol: Dairesel Ayarlar, Sağ: Kupa ve Yıldız Kapsülü)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    androidx.compose.material3.Surface(
                        onClick = onOpenSettings,
                        shape = CircleShape,
                        color = if (isDark) Color(0xFF1E2352) else Color(0xFFC8E6C9),
                        border = BorderStroke(
                            1.5.dp,
                            if (isDark) Color(0xFF3B4380) else Color(0xFFA5D6A7)
                        ),
                        modifier = Modifier.size(46.dp),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.menu_settings),
                                tint = if (isDark) Color.White else Color(0xFF1B4332),
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }

                    androidx.compose.material3.Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = if (isDark) Color(0xFF15193B) else Color(0xFFE8F5E9),
                        border = BorderStroke(
                            1.5.dp,
                            if (isDark) Color(0xFF3B4380) else Color(0xFFA5D6A7)
                        ),
                        modifier = Modifier.height(44.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(24.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${uiState.totalStars}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color.White else Color(0xFF1B4332),
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 2. HERO BÖLÜMÜ (3 Açılı Cam Tüp + Renkli Damlacıklar)
                HeroTubesIllustration(isDark = isDark)

                Spacer(modifier = Modifier.height(14.dp))

                // 3D "Renk Tüpleri" Başlığı
                if (isDark) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "Renk ",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 38.sp,
                                shadow = Shadow(
                                    color = Color(0xFF070A1E),
                                    offset = Offset(2f, 4f),
                                    blurRadius = 6f
                                )
                            ),
                            color = Color.White,
                        )
                        Text(
                            text = "Tüpleri",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 38.sp,
                                shadow = Shadow(
                                    color = Color(0xFF8F5A00),
                                    offset = Offset(2f, 4f),
                                    blurRadius = 6f
                                )
                            ),
                            color = Color(0xFFFFB300),
                        )
                    }
                } else {
                    Text(
                        text = "Renk Tüpleri",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Black,
                            fontSize = 38.sp,
                            shadow = Shadow(
                                color = Color(0xFF1B4332).copy(alpha = 0.30f),
                                offset = Offset(2f, 4f),
                                blurRadius = 4f
                            )
                        ),
                        color = Color(0xFF1B4332),
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = stringResource(R.string.menu_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) Color(0xFFA6B0DE) else Color(0xFF2E7D32),
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(28.dp))

                // 3. BÜYÜK 3D "BAŞLA" BUTONU
                Button(
                    onClick = { onPlayLevel(uiState.nextLevelId) },
                    contentPadding = PaddingValues(0.dp),
                    shape = RoundedCornerShape(32.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                ) {
                    val btnGradient = if (isDark) {
                        listOf(Color(0xFFFFC107), Color(0xFFFFA000))
                    } else {
                        listOf(Color(0xFF388E3C), Color(0xFF1B5E20))
                    }
                    val btnBorder = if (isDark) Color(0xFFFFD54F) else Color(0xFF81C784)

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(btnGradient),
                                shape = RoundedCornerShape(32.dp),
                            )
                            .border(BorderStroke(2.dp, btnBorder), shape = RoundedCornerShape(32.dp)),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color.White.copy(alpha = 0.28f), CircleShape),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp),
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (uiState.hasInProgress) {
                                    stringResource(R.string.menu_continue, uiState.nextLevelId)
                                } else {
                                    stringResource(R.string.menu_play_short)
                                },
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black,
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(26.dp))

                // 4. 3'LÜ DAİRESEL NAVİGASYON (Seviyeler, Başarılar, Tema)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularMenuItem(
                        icon = Icons.Default.Leaderboard,
                        label = stringResource(R.string.menu_levels),
                        isDark = isDark,
                        onClick = onOpenLevels,
                    )
                    CircularMenuItem(
                        icon = Icons.Default.EmojiEvents,
                        label = stringResource(R.string.menu_achievements_tab),
                        isDark = isDark,
                        onClick = { showAchievements = true },
                    )
                    CircularMenuItem(
                        icon = Icons.Default.Palette,
                        label = stringResource(R.string.menu_theme_tab),
                        isDark = isDark,
                        onClick = onOpenSettings,
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                // 5. NASIL OYNANIR? ALT KART
                androidx.compose.material3.Surface(
                    onClick = onOpenHowTo,
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDark) Color(0xFF15193B) else Color(0xFFE8F5E9),
                    border = BorderStroke(
                        1.5.dp,
                        if (isDark) Color(0xFF282F66) else Color(0xFFC8E6C9)
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(46.dp)
                                .background(
                                    if (isDark) Color(0xFF232959) else Color(0xFFC8E6C9),
                                    CircleShape
                                ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = null,
                                tint = Color(0xFFFFC107),
                                modifier = Modifier.size(26.dp),
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.menu_howto),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                color = if (isDark) Color.White else Color(0xFF1B4332),
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.menu_howto_hint),
                                style = MaterialTheme.typography.bodySmall,
                                color = if (isDark) Color(0xFFA6B0DE) else Color(0xFF2E7D32),
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                            contentDescription = null,
                            tint = if (isDark) Color(0xFFA6B0DE) else Color(0xFF2E7D32),
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }

                // 6. GÜNLÜK & DÜELLO HIZLI ERİŞİM ŞERİDİ
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    FilledTonalButton(
                        onClick = { showBotDifficultyDialog = true },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                    ) {
                        Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.menu_duel_bot),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                        )
                    }
                    FilledTonalButton(
                        onClick = { onPlayLevel(-1) },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (uiState.isDailyCompleted) stringResource(R.string.menu_daily_done)
                                   else stringResource(R.string.menu_daily_challenge),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                FilledTonalButton(
                    onClick = { showLanLobby = true },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                ) {
                    Icon(Icons.Default.Wifi, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.menu_duel_lan),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                    )
                }

            uiState.updateInfo?.let { update ->
                Spacer(modifier = Modifier.height(24.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f),
                            ) {
                                Icon(Icons.Default.SystemUpdate, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = stringResource(R.string.update_available_title, update.versionName),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                            when (val state = downloadState) {
                                is UpdateDownloadState.Idle -> {
                                    TextButton(
                                        onClick = {
                                            viewModel.downloadAndInstallUpdate(context, update.apkUrl)
                                        },
                                    ) {
                                        Text(text = stringResource(R.string.update_available_action))
                                    }
                                }
                                is UpdateDownloadState.Downloading -> {
                                    Text(
                                        text = stringResource(R.string.update_downloading, (state.progress * 100).toInt()),
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                                is UpdateDownloadState.ReadyToInstall -> {
                                    Button(
                                        onClick = {
                                            viewModel.installApkFile(context, state.file)
                                        },
                                    ) {
                                        Text(text = stringResource(R.string.update_install_btn))
                                    }
                                }
                                is UpdateDownloadState.Error -> {
                                    TextButton(
                                        onClick = {
                                            viewModel.downloadAndInstallUpdate(context, update.apkUrl)
                                        },
                                    ) {
                                        Text(text = stringResource(R.string.update_available_action))
                                    }
                                }
                            }
                        }
                        if (downloadState is UpdateDownloadState.Downloading) {
                            val dl = downloadState as UpdateDownloadState.Downloading
                            Spacer(modifier = Modifier.height(6.dp))
                            androidx.compose.material3.LinearProgressIndicator(
                                progress = { dl.progress },
                                modifier = Modifier.fillMaxWidth(),
                            )
                        } else if (downloadState is UpdateDownloadState.Error) {
                            val err = downloadState as UpdateDownloadState.Error
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.update_failed, err.message),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall,
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.menu_version_label, com.example.ballsort.BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        }
    }

    uiState.updateInfo?.let { update ->
        val showPopup = dismissedUpdateVersion != update.versionName
        if (showPopup) {
            AlertDialog(
                onDismissRequest = {
                    if (downloadState !is UpdateDownloadState.Downloading) {
                        dismissedUpdateVersion = update.versionName
                    }
                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.SystemUpdate,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp),
                    )
                },
                title = {
                    Text(
                        text = stringResource(R.string.update_dialog_title),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                    )
                },
                text = {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(
                            text = stringResource(R.string.update_dialog_desc, update.versionName),
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                        )
                        when (val state = downloadState) {
                            is UpdateDownloadState.Downloading -> {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    androidx.compose.material3.LinearProgressIndicator(
                                        progress = { state.progress },
                                        modifier = Modifier.fillMaxWidth(),
                                    )
                                    Text(
                                        text = stringResource(R.string.update_downloading, (state.progress * 100).toInt()),
                                        style = MaterialTheme.typography.labelMedium,
                                    )
                                }
                            }
                            is UpdateDownloadState.ReadyToInstall -> {
                                Text(
                                    text = stringResource(R.string.update_install_btn),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                            is UpdateDownloadState.Error -> {
                                Text(
                                    text = stringResource(R.string.update_failed, state.message),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                            is UpdateDownloadState.Idle -> { /* Başlangıç */ }
                        }
                    }
                },
                confirmButton = {
                    when (val state = downloadState) {
                        is UpdateDownloadState.ReadyToInstall -> {
                            Button(
                                onClick = {
                                    viewModel.installApkFile(context, state.file)
                                },
                            ) {
                                Text(text = stringResource(R.string.update_install_btn))
                            }
                        }
                        is UpdateDownloadState.Downloading -> { /* İndirilirken buton gizli veya devre dışı */ }
                        else -> {
                            Button(
                                onClick = {
                                    viewModel.downloadAndInstallUpdate(context, update.apkUrl)
                                },
                            ) {
                                Text(text = stringResource(R.string.update_start_btn))
                            }
                        }
                    }
                },
                dismissButton = {
                    if (downloadState !is UpdateDownloadState.Downloading) {
                        TextButton(
                            onClick = {
                                dismissedUpdateVersion = update.versionName
                            },
                        ) {
                            Text(text = stringResource(R.string.update_later_btn))
                        }
                    }
                },
            )
        }
    }

    if (showAchievements) {
        AlertDialog(
            onDismissRequest = { showAchievements = false },
            title = { Text(text = stringResource(R.string.achievements_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    uiState.achievements.forEach { item ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            Icon(
                                imageVector = if (item.unlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (item.unlocked) Color(0xFFFFC107) else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(22.dp),
                            )
                            Column {
                                Text(
                                    text = stringResource(item.titleRes),
                                    style = MaterialTheme.typography.titleSmall,
                                    color = if (item.unlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                                Text(
                                    text = stringResource(item.descRes),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAchievements = false }) {
                    Text(text = stringResource(R.string.close))
                }
            },
        )
    }

    if (showLanLobby) {
        var manualIp by remember { mutableStateOf("") }
        var manualPort by remember { mutableStateOf(LanDuelManager.DEFAULT_PORT.toString()) }
        val qrScannerLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
            val raw = result.contents ?: return@rememberLauncherForActivityResult
            val parsed = QrCodeHelper.parseEndpoint(raw) ?: return@rememberLauncherForActivityResult
            viewModel.connectLanDuel(parsed.first, parsed.second)
        }
        AlertDialog(
            onDismissRequest = {
                showLanLobby = false
                viewModel.closeLanDuel()
            },
            title = { Text(text = stringResource(R.string.lan_lobby_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { viewModel.hostLanDuel() },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(text = stringResource(R.string.lan_host_btn))
                        }
                        OutlinedButton(
                            onClick = { viewModel.browseLanDuel() },
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(text = stringResource(R.string.lan_join_btn))
                        }
                    }

                    when (val st = lanState) {
                        LanDuelState.Idle -> Unit
                        is LanDuelState.Hosting -> {
                            val qrBitmap = remember(st.localIp, st.port) {
                                QrCodeHelper.generateQrBitmap(QrCodeHelper.encodeEndpoint(st.localIp, st.port))
                            }
                            if (qrBitmap != null) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                ) {
                                    Image(
                                        bitmap = qrBitmap,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(176.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White)
                                            .padding(8.dp),
                                    )
                                }
                                Text(
                                    text = stringResource(R.string.lan_qr_hint),
                                    style = MaterialTheme.typography.bodySmall,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Text(
                                    text = stringResource(R.string.lan_waiting_guest),
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                            }
                            Text(
                                text = stringResource(R.string.lan_ip_port_hint, st.localIp, st.port),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        is LanDuelState.Browsing -> {
                            Button(
                                onClick = {
                                    qrScannerLauncher.launch(
                                        ScanOptions().apply {
                                            setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                                            setBeepEnabled(false)
                                            setOrientationLocked(true)
                                        }
                                    )
                                },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Icon(Icons.Default.QrCodeScanner, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = stringResource(R.string.lan_scan_qr_btn))
                            }
                            Text(
                                text = stringResource(R.string.lan_searching_rooms),
                                style = MaterialTheme.typography.bodySmall,
                            )
                            st.rooms.forEach { room ->
                                FilledTonalButton(
                                    onClick = { viewModel.connectLanDuel(room.host, room.port) },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(text = stringResource(R.string.lan_room_item, room.name, room.host, room.port))
                                }
                            }
                            OutlinedTextField(
                                value = manualIp,
                                onValueChange = { manualIp = it },
                                label = { Text(stringResource(R.string.lan_manual_ip_label)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                OutlinedTextField(
                                    value = manualPort,
                                    onValueChange = { manualPort = it },
                                    label = { Text(stringResource(R.string.lan_manual_port_label)) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                )
                                Button(
                                    enabled = manualIp.isNotBlank() && (manualPort.toIntOrNull() ?: 0) > 0,
                                    onClick = {
                                        val port = manualPort.toIntOrNull() ?: return@Button
                                        viewModel.connectLanDuel(manualIp, port)
                                    },
                                ) {
                                    Text(stringResource(R.string.lan_connect_btn))
                                }
                            }
                        }
                        LanDuelState.Connecting -> {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                Text(text = stringResource(R.string.lan_connecting))
                            }
                        }
                        is LanDuelState.Error -> {
                            Text(
                                text = st.message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall,
                            )
                        }
                        is LanDuelState.InMatch -> Unit
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLanLobby = false
                        viewModel.closeLanDuel()
                    },
                ) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
        )
    }

    if (showBotDifficultyDialog) {
        BotDifficultyDialog(
            onDismiss = { showBotDifficultyDialog = false },
            onSelectDifficulty = { diff ->
                showBotDifficultyDialog = false
                onPlayLevel(diff.levelId)
            },
        )
    }
}

@Composable
private fun BotDifficultyDialog(
    onDismiss: () -> Unit,
    onSelectDifficulty: (BotDifficulty) -> Unit,
) {
    val isDark = MaterialTheme.colorScheme.background.luminance() < 0.5f

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = if (isDark) Color(0xFFFFD54F) else Color(0xFFF57F17),
                )
                Text(
                    text = stringResource(R.string.bot_difficulty_dialog_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = stringResource(R.string.bot_difficulty_dialog_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                BotDifficulty.entries.forEach { diff ->
                    val (accentColor, containerColor) = when (diff) {
                        BotDifficulty.TRAINEE -> Color(0xFF26A69A) to (if (isDark) Color(0xFF143330) else Color(0xFFE0F2F1))
                        BotDifficulty.EASY -> Color(0xFF4CAF50) to (if (isDark) Color(0xFF1E3A24) else Color(0xFFE8F5E9))
                        BotDifficulty.MEDIUM -> Color(0xFFFFA000) to (if (isDark) Color(0xFF3E3014) else Color(0xFFFFF8E1))
                        BotDifficulty.HARD -> Color(0xFFE53935) to (if (isDark) Color(0xFF3E1919) else Color(0xFFFFEBEE))
                    }

                    Card(
                        onClick = { onSelectDifficulty(diff) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = containerColor),
                        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.6f)),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                ) {
                                    Text(
                                        text = stringResource(diff.titleRes),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else Color(0xFF1C1B1F),
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = accentColor.copy(alpha = 0.2f),
                                    ) {
                                        Text(
                                            text = stringResource(diff.badgeRes),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = accentColor,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stringResource(diff.descRes),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(24.dp),
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.cancel))
            }
        },
    )
}

@Composable
private fun HeroTubesIllustration(isDark: Boolean) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp),
    ) {
        // Renkli Damlacıklar & Işık Parıltısı
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f
            val cy = h / 2f

            // Arka plan yumuşak aura
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        (if (isDark) Color(0xFF4F8DFF) else Color(0xFFA5D6A7)).copy(alpha = 0.25f),
                        Color.Transparent,
                    ),
                    center = Offset(cx, cy),
                    radius = w * 0.35f,
                ),
                radius = w * 0.35f,
                center = Offset(cx, cy),
            )

            // Sıçrayan renkli damlacıklar (kırmızı, mavi, sarı, yeşil)
            drawCircle(Color(0xFFE53935), radius = 6.dp.toPx(), center = Offset(cx - 95.dp.toPx(), cy - 35.dp.toPx()))
            drawCircle(Color(0xFF1E88E5), radius = 5.dp.toPx(), center = Offset(cx + 90.dp.toPx(), cy - 40.dp.toPx()))
            drawCircle(Color(0xFFFDD835), radius = 7.dp.toPx(), center = Offset(cx - 75.dp.toPx(), cy - 60.dp.toPx()))
            drawCircle(Color(0xFF43A047), radius = 5.5f.dp.toPx(), center = Offset(cx + 80.dp.toPx(), cy - 15.dp.toPx()))
            drawCircle(Color(0xFF00ACC1), radius = 4.5f.dp.toPx(), center = Offset(cx - 50.dp.toPx(), cy - 68.dp.toPx()))
            drawCircle(Color(0xFFFB8C00), radius = 5.dp.toPx(), center = Offset(cx + 55.dp.toPx(), cy - 65.dp.toPx()))
        }

        // 3 Açılı Cam Tüp
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Sol Tüp (-9 derece eğik)
            CartoonTube(
                rotation = -9f,
                colors = listOf(Color(0xFFE53935), Color(0xFF1E88E5), Color(0xFFFDD835)),
                isDark = isDark,
            )
            // Orta Tüp (Düz)
            CartoonTube(
                rotation = 0f,
                colors = listOf(Color(0xFF43A047), Color(0xFFE53935), Color(0xFF1E88E5)),
                isDark = isDark,
            )
            // Sağ Tüp (+9 derece eğik)
            CartoonTube(
                rotation = 9f,
                colors = listOf(Color(0xFFFDD835), Color(0xFF43A047), Color(0xFFFDD835)),
                isDark = isDark,
            )
        }
    }
}

@Composable
private fun CartoonTube(
    rotation: Float,
    colors: List<Color>,
    isDark: Boolean,
) {
    val shape = RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp, bottomStart = 18.dp, bottomEnd = 18.dp)

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = Modifier
            .rotate(rotation)
            .width(38.dp)
            .height(96.dp)
            .clip(shape)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        Color.White.copy(alpha = if (isDark) 0.18f else 0.45f),
                        Color.White.copy(alpha = if (isDark) 0.06f else 0.18f),
                    )
                )
            )
            .border(
                BorderStroke(
                    2.dp,
                    if (isDark) Color.White.copy(alpha = 0.55f) else Color(0xFF2E7D32).copy(alpha = 0.40f),
                ),
                shape = shape,
            )
            .padding(bottom = 6.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            colors.forEach { ballColor ->
                GlossyBall(ballColor = ballColor, size = 26.dp)
            }
        }
    }
}

@Composable
private fun GlossyBall(ballColor: Color, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White.copy(alpha = 0.85f),
                        ballColor,
                        ballColor.copy(alpha = 0.85f),
                        Color.Black.copy(alpha = 0.45f),
                    ),
                    center = Offset(20f, 20f),
                    radius = 35f,
                ),
                shape = CircleShape,
            )
            .border(0.5.dp, Color.White.copy(alpha = 0.4f), CircleShape),
    )
}

@Composable
private fun CircularMenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isDark: Boolean,
    onClick: () -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        androidx.compose.material3.Surface(
            onClick = onClick,
            shape = CircleShape,
            color = if (isDark) Color(0xFF1D2254) else Color(0xFFC8E6C9),
            border = BorderStroke(
                1.5.dp,
                if (isDark) Color(0xFF3B4380) else Color(0xFFA5D6A7),
            ),
            modifier = Modifier.size(62.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isDark) Color(0xFFFFC107) else Color(0xFF1B4332),
                    modifier = Modifier.size(28.dp),
                )
            }
        }
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isDark) Color(0xFFA6B0DE) else Color(0xFF1B4332),
        )
    }
}

