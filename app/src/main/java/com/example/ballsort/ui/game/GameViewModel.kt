/*
 * com.example.ballsort.ui.game
 * Oyun ekranı ViewModel'i: seviye üretimi, hamle mantığı, ipucu çözücü, sayaç ve kayıt.
 */
package com.example.ballsort.ui.game

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballsort.data.InProgress
import com.example.ballsort.data.LevelResult
import com.example.ballsort.data.MoveDto
import com.example.ballsort.data.ProgressRepository
import com.example.ballsort.data.Settings
import com.example.ballsort.data.SettingsRepository
import com.example.ballsort.di.DefaultDispatcher
import com.example.ballsort.domain.DifficultyCurve
import com.example.ballsort.domain.GameLogic
import com.example.ballsort.domain.LevelGenerator
import com.example.ballsort.domain.Solver
import com.example.ballsort.domain.StarCalculator
import com.example.ballsort.domain.model.BotDifficulty
import com.example.ballsort.domain.model.GameStatus
import com.example.ballsort.domain.model.LevelConfig
import com.example.ballsort.domain.model.Move
import com.example.ballsort.domain.model.Tube
import com.example.ballsort.platform.LanDuelState
import com.example.ballsort.platform.LanDuelTransport
import com.example.ballsort.util.currentEpochDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val lanDuelTransport: LanDuelTransport,
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {

    private val initialLevelId: Int = savedStateHandle.get<Int>("levelId") ?: 1

    private val _uiState = MutableStateFlow(GameUiState(levelId = initialLevelId))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<GameEffect>(extraBufferCapacity = 16)
    val effects: SharedFlow<GameEffect> = _effects.asSharedFlow()

    private val history = mutableListOf<Move>()
    private var currentConfig: LevelConfig = configForRoute(initialLevelId)
    private var currentSettings: Settings = Settings()
    private var hintsUsed: Int = 0
    private var extraTubeUsed: Boolean = false
    private var isScreenActive: Boolean = true
    private var timerJob: Job? = null
    private var botJob: Job? = null
    private var pendingEndStatus: GameStatus? = null
    private var transferSeq: Long = 0L
    private var shakeSeq: Long = 0L

    init {
        observeSettings()
        observeLanDuel()
        loadLevel(initialLevelId, forceFresh = false)
    }

    private fun observeLanDuel() {
        viewModelScope.launch {
            lanDuelTransport.state.collect { lan ->
                if (!_uiState.value.isLanDuel) return@collect
                if (lan is LanDuelState.InMatch) {
                    if (lan.opponentWon && _uiState.value.status == GameStatus.Playing) {
                        timerJob?.cancel()
                        _uiState.update {
                            it.copy(
                                lanOpponentCompleted = lan.opponentCompleted,
                                lanOpponentTotal = lan.opponentTotal,
                                lanOpponentMoves = lan.opponentMoves,
                                duelBotWon = true,
                                status = GameStatus.Stuck,
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                lanOpponentCompleted = lan.opponentCompleted,
                                lanOpponentTotal = lan.opponentTotal,
                                lanOpponentMoves = lan.opponentMoves,
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings ->
                currentSettings = settings
                _uiState.update {
                    it.copy(
                        colorBlind = settings.colorBlind,
                        ballSkin = settings.ballSkin,
                        tubeSkin = settings.tubeSkin,
                    )
                }
            }
        }
    }

    private fun configForRoute(levelId: Int): LevelConfig = when {
        levelId == -1 -> DifficultyCurve.dailyConfig(currentEpochDay())
        BotDifficulty.isBotDuel(levelId) -> DifficultyCurve.duelConfig(difficulty = BotDifficulty.fromLevelId(levelId))
        levelId == -3 -> DifficultyCurve.duelConfig(
            (lanDuelTransport.state.value as? LanDuelState.InMatch)?.seed ?: System.currentTimeMillis()
        )
        else -> DifficultyCurve.configFor(levelId)
    }

    private fun loadLevel(levelId: Int, forceFresh: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            botJob?.cancel()
            val isDaily = levelId == -1
            val isDuel = BotDifficulty.isBotDuel(levelId)
            val botDifficulty = if (isDuel) BotDifficulty.fromLevelId(levelId) else null
            val isLanDuel = levelId == -3
            val epochDay = if (isDaily) currentEpochDay() else null
            currentConfig = when {
                isDaily -> DifficultyCurve.dailyConfig(epochDay!!)
                isDuel -> DifficultyCurve.duelConfig(difficulty = botDifficulty ?: BotDifficulty.MEDIUM)
                isLanDuel -> DifficultyCurve.duelConfig(
                    (lanDuelTransport.state.value as? LanDuelState.InMatch)?.seed ?: System.currentTimeMillis()
                )
                else -> DifficultyCurve.configFor(levelId)
            }

            val saved = if (isDuel || isLanDuel) null else progressRepository.saveData.first().inProgress
            val canResume = !forceFresh && !isDuel && !isLanDuel && saved != null && (
                (isDaily && saved.dailyEpochDay == epochDay) ||
                    (!isDaily && saved.levelId == levelId && saved.dailyEpochDay == null)
                )

            history.clear()
            pendingEndStatus = null

            if (canResume) {
                val nonNullSaved = saved ?: return@launch
                history.addAll(nonNullSaved.history.map { Move(it.from, it.to, it.count) })
                hintsUsed = nonNullSaved.hintsUsed
                extraTubeUsed = nonNullSaved.extraTubeUsed
                val maxMoves = currentConfig.maxMoves
                val moveLimitReached = maxMoves != null && nonNullSaved.moves >= maxMoves
                val resumedStatus = when {
                    GameLogic.isSolved(nonNullSaved.tubes, currentConfig.capacity) -> GameStatus.Won
                    moveLimitReached || GameLogic.isStuck(nonNullSaved.tubes, currentConfig.capacity) -> GameStatus.Stuck
                    else -> GameStatus.Playing
                }
                _uiState.value = GameUiState(
                    levelId = levelId,
                    isDaily = isDaily,
                    capacity = currentConfig.capacity,
                    tubes = nonNullSaved.tubes,
                    selectedTube = null,
                    moves = nonNullSaved.moves,
                    maxMoves = currentConfig.maxMoves,
                    elapsedMs = saved.elapsedMs,
                    canUndo = history.isNotEmpty(),
                    extraTubeAvailable = !extraTubeUsed,
                    status = resumedStatus,
                    isLoading = false,
                    colorBlind = currentSettings.colorBlind,
                    ballSkin = currentSettings.ballSkin,
                    tubeSkin = currentSettings.tubeSkin,
                    lockedTubeIndex = computeLockedTube(levelId, saved.tubes, currentConfig.capacity),
                )
            } else {
                val tubes = withContext(defaultDispatcher) {
                    LevelGenerator.generate(currentConfig)
                }
                val botTotal = if (isDuel) {
                    val optimalMoves = Solver.solve(
                        tubes = tubes,
                        capacity = currentConfig.capacity,
                        multiMove = true,
                        timeoutMs = 1_500L,
                        dispatcher = defaultDispatcher,
                    )?.size ?: StarCalculator.fallbackOptimalMoves(
                        colorCount = currentConfig.colorCount,
                        capacity = currentConfig.capacity,
                    )
                    val mult = botDifficulty?.moveMultiplier ?: 1.0f
                    (optimalMoves * mult).toInt().coerceAtLeast(optimalMoves)
                } else {
                    0
                }
                hintsUsed = 0
                extraTubeUsed = false
                _uiState.value = GameUiState(
                    levelId = levelId,
                    isDaily = isDaily,
                    isDuel = isDuel,
                    isLanDuel = isLanDuel,
                    botDifficulty = botDifficulty,
                    botMoves = 0,
                    botTotalMoves = botTotal,
                    lanOpponentTotal = currentConfig.colorCount,
                    duelBotWon = false,
                    capacity = currentConfig.capacity,
                    tubes = tubes,
                    selectedTube = null,
                    moves = 0,
                    maxMoves = currentConfig.maxMoves,
                    elapsedMs = 0L,
                    canUndo = false,
                    extraTubeAvailable = true,
                    status = GameStatus.Playing,
                    isLoading = false,
                    colorBlind = currentSettings.colorBlind,
                    ballSkin = currentSettings.ballSkin,
                    tubeSkin = currentSettings.tubeSkin,
                    lockedTubeIndex = computeLockedTube(levelId, tubes, currentConfig.capacity),
                )
                persistInProgress()
            }
            startTimerIfNeeded()
            startBotDuelIfNeeded()
        }
    }

    fun onEvent(event: GameEvent) {
        when (event) {
            is GameEvent.TubeTapped -> handleTubeTap(event.index)
            GameEvent.Undo -> handleUndo()
            GameEvent.Restart -> handleRestart()
            GameEvent.AddTube -> handleAddTube()
            GameEvent.RequestHint -> handleHint()
            GameEvent.NextLevel -> handleNextLevel()
            GameEvent.AnimationFinished -> handleAnimationFinished()
            GameEvent.ScreenResumed -> {
                isScreenActive = true
                startTimerIfNeeded()
                startBotDuelIfNeeded()
            }
            GameEvent.ScreenPaused -> {
                isScreenActive = false
                timerJob?.cancel()
                botJob?.cancel()
                viewModelScope.launch { persistInProgress() }
            }
        }
    }

    private fun handleTubeTap(index: Int) {
        val state = _uiState.value
        if (state.isLoading || state.isAnimating || state.status != GameStatus.Playing) return
        if (index !in state.tubes.indices) return

        if (index == state.lockedTubeIndex) {
            shakeSeq++
            _uiState.update {
                it.copy(
                    selectedTube = null,
                    invalidShakeTube = index,
                    invalidShakeToken = shakeSeq,
                )
            }
            _effects.tryEmit(GameEffect.InvalidMove)
            return
        }

        val selected = state.selectedTube
        if (selected == null) {
            if (state.tubes[index].isNotEmpty()) {
                _uiState.update { it.copy(selectedTube = index, hintMove = null) }
                _effects.tryEmit(GameEffect.SelectTube)
            }
            return
        }

        if (selected == index) {
            _uiState.update { it.copy(selectedTube = null) }
            return
        }

        val movedColor = state.tubes[selected].lastOrNull() ?: return
        val targetSlotBefore = state.tubes[index].size
        val result = GameLogic.applyMove(
            tubes = state.tubes,
            from = selected,
            to = index,
            capacity = state.capacity,
            multiMove = currentSettings.multiMove,
        )

        if (result == null) {
            // Geçersiz hamle -> seçimi temizle ve salla
            shakeSeq++
            _uiState.update {
                it.copy(
                    selectedTube = null,
                    invalidShakeTube = index,
                    invalidShakeToken = shakeSeq,
                )
            }
            _effects.tryEmit(GameEffect.InvalidMove)
            return
        }

        val (nextTubes, move) = result
        history.add(move)
        val nextMoves = state.moves + 1
        val completedTube = GameLogic.isTubeCompleted(nextTubes[index], state.capacity)
        val won = GameLogic.isSolved(nextTubes, state.capacity)
        val moveLimitReached = state.maxMoves != null && nextMoves >= state.maxMoves
        val stuck = !won && (moveLimitReached || GameLogic.isStuck(nextTubes, state.capacity))

        pendingEndStatus = when {
            won -> GameStatus.Won
            stuck -> GameStatus.Stuck
            else -> null
        }

        transferSeq++
        _uiState.update {
            it.copy(
                tubes = nextTubes,
                selectedTube = null,
                moves = nextMoves,
                canUndo = true,
                hintMove = null,
                isAnimating = true,
                lockedTubeIndex = computeLockedTube(state.levelId, nextTubes, state.capacity),
                activeTransfer = TransferAnimation(
                    id = transferSeq,
                    from = move.from,
                    to = move.to,
                    colorId = movedColor,
                    count = move.count,
                    targetStartSlot = targetSlotBefore,
                ),
            )
        }

        if (completedTube && !won) {
            _effects.tryEmit(GameEffect.TubeCompleted)
        } else {
            _effects.tryEmit(GameEffect.BallPlaced)
        }

        if (state.isLanDuel) {
            val completedCount = nextTubes.count { GameLogic.isTubeCompleted(it, state.capacity) }
            lanDuelTransport.sendProgress(
                completedTubes = completedCount,
                totalColors = currentConfig.colorCount,
                moves = nextMoves,
                won = won,
            )
        }

        viewModelScope.launch {
            persistInProgress()
        }
    }

    private fun handleAnimationFinished() {
        val state = _uiState.value
        if (!state.isAnimating && state.activeTransfer == null) return

        val endStatus = pendingEndStatus
        pendingEndStatus = null

        if (endStatus == GameStatus.Won) {
            timerJob?.cancel()
            botJob?.cancel()
            _uiState.update {
                it.copy(
                    isAnimating = false,
                    activeTransfer = null,
                    status = GameStatus.Won,
                )
            }
            _effects.tryEmit(GameEffect.LevelWon)
            finalizeWin(state.tubes, state.moves, state.elapsedMs)
        } else if (endStatus == GameStatus.Stuck) {
            timerJob?.cancel()
            _uiState.update {
                it.copy(
                    isAnimating = false,
                    activeTransfer = null,
                    status = GameStatus.Stuck,
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isAnimating = false,
                    activeTransfer = null,
                )
            }
        }
    }

    private fun finalizeWin(tubes: List<Tube>, moves: Int, elapsedMs: Long) {
        viewModelScope.launch {
            // Başlangıç tahtasına göre optimal hamle tahmini (daha doğru yıldız için)
            val initialTubes = withContext(defaultDispatcher) {
                LevelGenerator.generate(currentConfig)
            }
            val optimal = Solver.solve(
                tubes = initialTubes,
                capacity = currentConfig.capacity,
                multiMove = currentSettings.multiMove,
                timeoutMs = 1_000L,
                dispatcher = defaultDispatcher,
            )?.size ?: StarCalculator.fallbackOptimalMoves(
                colorCount = currentConfig.colorCount,
                capacity = currentConfig.capacity,
            )

            val stars = StarCalculator.calcStars(
                moves = moves,
                optimalMoves = optimal,
                hintsUsed = hintsUsed,
            )
            _uiState.update { it.copy(stars = stars) }

            if (_uiState.value.isDuel || _uiState.value.isLanDuel) {
                progressRepository.recordDuelWin()
                return@launch
            }
            if (hintsUsed == 0) {
                progressRepository.recordNoHintWin()
            }
            if (_uiState.value.isDaily) {
                progressRepository.saveDailyCompleted(currentEpochDay())
            } else {
                progressRepository.saveResult(
                    levelId = currentConfig.levelId,
                    result = LevelResult(
                        stars = stars,
                        bestMoves = moves,
                        bestTimeMs = elapsedMs,
                    ),
                )
            }
        }
    }

    private fun handleUndo() {
        val state = _uiState.value
        if (state.isAnimating || state.isLoading || history.isEmpty()) return
        if (state.status == GameStatus.Won) return

        val lastMove = history.removeAt(history.lastIndex)
        val restored = GameLogic.undoMove(state.tubes, lastMove)
        _uiState.update {
            it.copy(
                tubes = restored,
                selectedTube = null,
                canUndo = history.isNotEmpty(),
                status = GameStatus.Playing,
                hintMove = null,
                lockedTubeIndex = computeLockedTube(state.levelId, restored, state.capacity),
            )
        }
        startTimerIfNeeded()
        viewModelScope.launch { persistInProgress() }
    }

    private fun handleRestart() {
        val state = _uiState.value
        if (state.isAnimating || state.isLoading) return
        loadLevel(state.levelId, forceFresh = true)
    }

    private fun handleAddTube() {
        val state = _uiState.value
        if (state.isAnimating || state.isLoading || !state.extraTubeAvailable) return
        if (state.status == GameStatus.Won) return

        extraTubeUsed = true
        val updatedTubes = state.tubes + listOf(emptyList())
        _uiState.update {
            it.copy(
                tubes = updatedTubes,
                extraTubeAvailable = false,
                selectedTube = null,
                status = GameStatus.Playing,
                hintMove = null,
                lockedTubeIndex = computeLockedTube(state.levelId, updatedTubes, state.capacity),
            )
        }
        startTimerIfNeeded()
        viewModelScope.launch { persistInProgress() }
    }

    private fun handleHint() {
        val state = _uiState.value
        if (state.isAnimating || state.isLoading || state.isSolvingHint) return
        if (state.status != GameStatus.Playing) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSolvingHint = true) }
            val move = Solver.hint(
                tubes = state.tubes,
                capacity = state.capacity,
                multiMove = currentSettings.multiMove,
                timeoutMs = 2_000L,
                dispatcher = defaultDispatcher,
            )
            if (move != null) {
                hintsUsed++
            }
            _uiState.update {
                it.copy(
                    isSolvingHint = false,
                    hintMove = move?.let { m -> m.from to m.to },
                )
            }
            persistInProgress()
        }
    }

    private fun handleNextLevel() {
        val state = _uiState.value
        val nextId = when {
            state.isDuel -> state.levelId
            state.isDaily -> 1
            else -> state.levelId + 1
        }
        loadLevel(nextId, forceFresh = true)
    }

    private fun startBotDuelIfNeeded() {
        botJob?.cancel()
        val state = _uiState.value
        if (!isScreenActive || !state.isDuel || state.status != GameStatus.Playing || state.isLoading || state.botTotalMoves <= 0) {
            return
        }
        val delayMs = state.botDifficulty?.delayMs ?: 1_200L
        botJob = viewModelScope.launch {
            while (true) {
                delay(delayMs)
                val cur = _uiState.value
                if (!isScreenActive || cur.status != GameStatus.Playing) continue
                val nextBotMoves = cur.botMoves + 1
                if (nextBotMoves >= cur.botTotalMoves) {
                    timerJob?.cancel()
                    _uiState.update {
                        it.copy(
                            botMoves = cur.botTotalMoves,
                            duelBotWon = true,
                            status = GameStatus.Stuck,
                        )
                    }
                    break
                } else {
                    _uiState.update { it.copy(botMoves = nextBotMoves) }
                }
            }
        }
    }

    private fun startTimerIfNeeded() {
        timerJob?.cancel()
        if (!isScreenActive || _uiState.value.status != GameStatus.Playing || _uiState.value.isLoading) {
            return
        }
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1_000L)
                if (_uiState.value.status == GameStatus.Playing && isScreenActive) {
                    _uiState.update { it.copy(elapsedMs = it.elapsedMs + 1_000L) }
                }
            }
        }
    }

    private fun computeLockedTube(levelId: Int, tubes: List<Tube>, capacity: Int): Int? = null

    private suspend fun persistInProgress() {
        val state = _uiState.value
        if (state.isDuel || state.isLanDuel || state.tubes.isEmpty() || state.status == GameStatus.Won) return
        progressRepository.saveInProgress(
            InProgress(
                levelId = state.levelId,
                tubes = state.tubes,
                moves = state.moves,
                elapsedMs = state.elapsedMs,
                history = history.map { MoveDto(it.from, it.to, it.count) },
                extraTubeUsed = extraTubeUsed,
                hintsUsed = hintsUsed,
                dailyEpochDay = if (state.isDaily) currentEpochDay() else null,
            )
        )
    }

    override fun onCleared() {
        timerJob?.cancel()
        botJob?.cancel()
        if (_uiState.value.isLanDuel) {
            lanDuelTransport.close()
        }
        super.onCleared()
    }
}

