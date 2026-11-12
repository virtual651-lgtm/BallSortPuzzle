/*
 * com.example.ballsort.ui.game
 * Oyun ekranı: uyarlanabilir çok satırlı tüp yerleşimi, uçuş animasyonu, konfeti ve diyaloglar.
 */
package com.example.ballsort.ui.game

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ballsort.R
import com.example.ballsort.data.Settings
import com.example.ballsort.domain.model.GameStatus
import com.example.ballsort.platform.Haptics
import com.example.ballsort.platform.SoundPlayer
import com.example.ballsort.ui.components.ConfettiOverlay
import com.example.ballsort.ui.components.FlyingBallOverlay
import com.example.ballsort.ui.components.GameBackdrop
import com.example.ballsort.ui.components.GameBottomBar
import com.example.ballsort.ui.components.GameTopBar
import com.example.ballsort.ui.components.StuckDialog
import com.example.ballsort.ui.components.TubeView
import com.example.ballsort.ui.components.WinDialog
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.collectLatest

@EntryPoint
@InstallIn(SingletonComponent::class)
interface GameFeedbackEntryPoint {
    fun soundPlayer(): SoundPlayer
    fun haptics(): Haptics
    fun settingsRepository(): com.example.ballsort.data.SettingsRepository
}

@Composable
fun GameScreen(
    onNavigateMenu: () -> Unit,
    viewModel: GameViewModel = hiltViewModel(),
) {
    val context = LocalContext.current.applicationContext
    val entryPoint = remember(context) {
        EntryPointAccessors.fromApplication(context, GameFeedbackEntryPoint::class.java)
    }
    val soundPlayer = remember { entryPoint.soundPlayer() }
    val haptics = remember { entryPoint.haptics() }
    val settings by entryPoint.settingsRepository().settings.collectAsStateWithLifecycle(
        initialValue = Settings()
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showExitConfirm by remember { mutableStateOf(false) }

    // Yaşam döngüsü: arka plana gidince sayacı durdur ve ilerlemeyi kaydet
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.onEvent(GameEvent.ScreenResumed)
                Lifecycle.Event.ON_PAUSE -> viewModel.onEvent(GameEvent.ScreenPaused)
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Tek seferlik ses ve titreşim efektleri
    LaunchedEffect(viewModel) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                GameEffect.SelectTube -> {
                    soundPlayer.playSelect(settings.sound)
                    haptics.click(settings.vibration)
                }
                GameEffect.BallPlaced -> {
                    soundPlayer.playPlace(settings.sound)
                    haptics.click(settings.vibration)
                }
                GameEffect.TubeCompleted -> {
                    soundPlayer.playTubeCompleted(settings.sound)
                    haptics.tubeComplete(settings.vibration)
                }
                GameEffect.InvalidMove -> {
                    soundPlayer.playInvalid(settings.sound)
                    haptics.error(settings.vibration)
                }
                GameEffect.LevelWon -> {
                    soundPlayer.playWin(settings.sound)
                    haptics.win(settings.vibration)
                }
            }
        }
    }

    BackHandler(enabled = uiState.status == GameStatus.Playing) {
        showExitConfirm = true
    }

    GameScreenContent(
        uiState = uiState,
        onEvent = viewModel::onEvent,
        onBackClick = {
            if (uiState.status == GameStatus.Playing && uiState.moves > 0) {
                showExitConfirm = true
            } else {
                onNavigateMenu()
            }
        },
        onNavigateMenu = onNavigateMenu,
    )

    if (showExitConfirm) {
        AlertDialog(
            onDismissRequest = { showExitConfirm = false },
            title = { Text(text = stringResource(R.string.exit_dialog_title)) },
            text = { Text(text = stringResource(R.string.exit_dialog_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        showExitConfirm = false
                        viewModel.onEvent(GameEvent.ScreenPaused)
                        onNavigateMenu()
                    },
                ) {
                    Text(text = stringResource(R.string.exit_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showExitConfirm = false }) {
                    Text(text = stringResource(R.string.cancel))
                }
            },
        )
    }
}

@Composable
fun GameScreenContent(
    uiState: GameUiState,
    onEvent: (GameEvent) -> Unit,
    onBackClick: () -> Unit,
    onNavigateMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val slotPoints = remember { mutableStateMapOf<Pair<Int, Int>, Offset>() }
    val topExitPoints = remember { mutableStateMapOf<Int, Offset>() }
    var boardRootTopLeft by remember { mutableStateOf(Offset.Zero) }

    GameBackdrop(modifier = modifier.fillMaxSize()) {
        Scaffold(
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing),
        topBar = {
            GameTopBar(
                levelId = uiState.levelId,
                isDaily = uiState.isDaily,
                isDuel = uiState.isDuel,
                botMoves = uiState.botMoves,
                botTotalMoves = uiState.botTotalMoves,
                isLanDuel = uiState.isLanDuel,
                lanOpponentCompleted = uiState.lanOpponentCompleted,
                lanOpponentTotal = uiState.lanOpponentTotal,
                lanOpponentMoves = uiState.lanOpponentMoves,
                moves = uiState.moves,
                maxMoves = uiState.maxMoves,
                elapsedMs = uiState.elapsedMs,
                onBackClick = onBackClick,
            )
        },
        bottomBar = {
            GameBottomBar(
                canUndo = uiState.canUndo && !uiState.isAnimating,
                extraTubeAvailable = uiState.extraTubeAvailable && !uiState.isAnimating,
                isSolvingHint = uiState.isSolvingHint,
                onUndo = { onEvent(GameEvent.Undo) },
                onRestart = { onEvent(GameEvent.Restart) },
                onHint = { onEvent(GameEvent.RequestHint) },
                onAddTube = { onEvent(GameEvent.AddTube) },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .onGloballyPositioned { coords ->
                    boardRootTopLeft = coords.positionInRoot()
                },
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                BoxWithConstraints(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                ) {
                    val tubeCount = uiState.tubes.size
                    val rowCount = when {
                        tubeCount <= 5 -> 1
                        tubeCount <= 10 -> 2
                        else -> 3
                    }
                    val maxPerRow = (tubeCount + rowCount - 1) / rowCount
                    val rows = uiState.tubes.withIndex().chunked(maxPerRow)
                    val tubeHorizontalGap = if (maxPerRow >= 5) 6.dp else 10.dp
                    val rowVerticalGap = if (rowCount >= 3) 10.dp else 16.dp

                    val (tubeWidth, ballSize) = computeDimensions(
                        availableWidth = maxWidth,
                        availableHeight = maxHeight,
                        maxPerRow = maxPerRow,
                        rowCount = rows.size,
                        capacity = uiState.capacity,
                        tubeHorizontalGap = tubeHorizontalGap,
                        rowVerticalGap = rowVerticalGap,
                    )

                    Column(
                        verticalArrangement = Arrangement.spacedBy(rowVerticalGap, Alignment.CenterVertically),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize(),
                    ) {
                        rows.forEach { rowItems ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(tubeHorizontalGap, Alignment.CenterHorizontally),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                rowItems.forEach { indexedTube ->
                                    val idx = indexedTube.index
                                    val transfer = uiState.activeTransfer
                                    val hiddenSlots = if (transfer != null && transfer.to == idx) {
                                        transfer.targetStartSlot until (transfer.targetStartSlot + transfer.count)
                                    } else {
                                        IntRange.EMPTY
                                    }

                                    TubeView(
                                        index = idx,
                                        tube = indexedTube.value,
                                        capacity = uiState.capacity,
                                        tubeWidth = tubeWidth,
                                        ballSize = ballSize,
                                        isSelected = uiState.selectedTube == idx,
                                        isHintSource = uiState.hintMove?.first == idx,
                                        isHintTarget = uiState.hintMove?.second == idx,
                                        shakeToken = if (uiState.invalidShakeTube == idx) uiState.invalidShakeToken else 0L,
                                        hiddenSlots = hiddenSlots,
                                        colorBlind = uiState.colorBlind,
                                        ballSkin = uiState.ballSkin,
                                        tubeSkin = uiState.tubeSkin,
                                        isLocked = uiState.lockedTubeIndex == idx,
                                        onSlotMeasured = { slot, center ->
                                            slotPoints[idx to slot] = center
                                        },
                                        onTopExitMeasured = { center ->
                                            topExitPoints[idx] = center
                                        },
                                        onClick = { onEvent(GameEvent.TubeTapped(idx)) },
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    // Uçan top animasyon katmanı
                    FlyingBallOverlay(
                        transfer = uiState.activeTransfer,
                        ballSize = ballSize,
                        colorBlind = uiState.colorBlind,
                        ballSkin = uiState.ballSkin,
                        rootTopLeft = boardRootTopLeft,
                        topExitPoints = topExitPoints,
                        slotPoints = slotPoints,
                        onFinished = { onEvent(GameEvent.AnimationFinished) },
                    )
                }
            }

            ConfettiOverlay(visible = uiState.status == GameStatus.Won)
        }
        }
    }

    when (uiState.status) {
        GameStatus.Won -> {
            WinDialog(
                levelId = uiState.levelId,
                isDaily = uiState.isDaily,
                isDuel = uiState.isDuel || uiState.isLanDuel,
                stars = uiState.stars,
                moves = uiState.moves,
                elapsedMs = uiState.elapsedMs,
                onNextLevel = { onEvent(GameEvent.NextLevel) },
                onReplay = { onEvent(GameEvent.Restart) },
                onMenu = onNavigateMenu,
            )
        }
        GameStatus.Stuck -> {
            StuckDialog(
                canUndo = uiState.canUndo && !uiState.duelBotWon,
                extraTubeAvailable = uiState.extraTubeAvailable,
                onUndo = { onEvent(GameEvent.Undo) },
                onRestart = { onEvent(GameEvent.Restart) },
                onAddTube = { onEvent(GameEvent.AddTube) },
                isMoveLimitReached = uiState.maxMoves != null && uiState.moves >= uiState.maxMoves,
                isDuelBotWon = uiState.duelBotWon,
            )
        }
        GameStatus.Playing -> Unit
    }
}

private fun computeDimensions(
    availableWidth: Dp,
    availableHeight: Dp,
    maxPerRow: Int,
    rowCount: Int,
    capacity: Int,
    tubeHorizontalGap: Dp,
    rowVerticalGap: Dp,
): Pair<Dp, Dp> {
    // TubeView dışındaki horizontal padding (2.dp * 2) her tüpün toplam genişliğine eklenir
    val tubeOuterHorizontalPadding = 4.dp
    val totalHorizontalOverhead = tubeHorizontalGap * (maxPerRow - 1) + tubeOuterHorizontalPadding * maxPerRow
    val maxTubeW = ((availableWidth - totalHorizontalOverhead) / maxPerRow).coerceIn(32.dp, 68.dp)

    // TubeView dikey sabitleri: üst çıkış (22.dp) + gövde dolgusu (8.dp) + slot aralıkları (4.dp * (capacity + 1))
    val perTubeVerticalOverhead = 22.dp + 8.dp + 4.dp * (capacity + 1)
    val totalVerticalOverhead = rowVerticalGap * (rowCount - 1) + perTubeVerticalOverhead * rowCount + 4.dp
    val maxBallByHeight = ((availableHeight - totalVerticalOverhead) / (rowCount * capacity)).coerceIn(16.dp, 46.dp)
    val maxBallByWidth = (maxTubeW - 14.dp).coerceIn(16.dp, 46.dp)

    val ballSize = minOf(maxBallByHeight, maxBallByWidth)
    val tubeWidth = (ballSize + 14.dp).coerceAtMost(maxTubeW)
    return tubeWidth to ballSize
}

