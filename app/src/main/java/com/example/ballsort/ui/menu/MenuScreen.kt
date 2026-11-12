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
import androidx.compose.ui.draw.clip
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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
        Scaffold(
            containerColor = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                MiniTubesLogo()
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = stringResource(R.string.menu_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(20.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 8.dp),
                ) {
                    StatBadge(
                        icon = Icons.Default.Star,
                        tint = Color(0xFFFFC107),
                        label = stringResource(R.string.stats_stars, uiState.totalStars),
                    )
                    StatBadge(
                        icon = Icons.Default.CheckCircle,
                        tint = MaterialTheme.colorScheme.primary,
                        label = stringResource(R.string.stats_levels, uiState.completedLevels),
                    )
                    StatBadge(
                        icon = Icons.Default.LocalFireDepartment,
                        tint = Color(0xFFFF7043),
                        label = stringResource(R.string.stats_streak, uiState.dailyStreak),
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onPlayLevel(uiState.nextLevelId) },
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary,
                                ),
                            ),
                            shape = RoundedCornerShape(28.dp),
                        ),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (uiState.hasInProgress) {
                                stringResource(R.string.menu_continue, uiState.nextLevelId)
                            } else {
                                stringResource(R.string.menu_play, uiState.nextLevelId)
                            },
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            FilledTonalButton(
                onClick = { onPlayLevel(-1) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
            ) {
                Icon(Icons.Default.CalendarToday, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (uiState.isDailyCompleted) {
                        stringResource(R.string.menu_daily_done)
                    } else {
                        stringResource(R.string.menu_daily_challenge)
                    },
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FilledTonalButton(
                onClick = { onPlayLevel(-2) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
            ) {
                Icon(Icons.Default.Bolt, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.menu_duel_bot))
            }

            Spacer(modifier = Modifier.height(10.dp))

            FilledTonalButton(
                onClick = { showLanLobby = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
            ) {
                Icon(Icons.Default.Wifi, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.menu_duel_lan))
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onOpenLevels,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
            ) {
                Icon(Icons.Default.GridView, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.menu_levels))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                OutlinedButton(
                    onClick = onOpenHowTo,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(1.15f)
                        .height(48.dp),
                ) {
                    Icon(Icons.AutoMirrored.Filled.HelpOutline, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.menu_howto),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                OutlinedButton(
                    onClick = onOpenSettings,
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier
                        .weight(0.85f)
                        .height(48.dp),
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = stringResource(R.string.menu_settings),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val unlockedCount = uiState.achievements.count { it.unlocked }
            OutlinedButton(
                onClick = { showAchievements = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
            ) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFFFFC107))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.menu_achievements, unlockedCount, uiState.achievements.size))
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
}

@Composable
private fun MiniTubesLogo() {
    val tubeSpecs = listOf(
        listOf(BallPalette[0].color, BallPalette[1].color, BallPalette[2].color),
        listOf(BallPalette[3].color, BallPalette[0].color, BallPalette[1].color),
        listOf(BallPalette[2].color, BallPalette[3].color),
    )
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        tubeSpecs.forEach { balls ->
            val shape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 14.dp, bottomEnd = 14.dp)
            Box(
                contentAlignment = Alignment.BottomCenter,
                modifier = Modifier
                    .width(26.dp)
                    .height(58.dp)
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.45f))
                    .border(
                        BorderStroke(1.5.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f)),
                        shape = shape,
                    )
                    .padding(bottom = 4.dp),
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    balls.forEach { ballColor ->
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .background(ballColor, CircleShape),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    label: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

