/*
 * com.example.ballsort.ui.game
 * Oyun ekranının tek yönlü veri akışı (UDF) için değişmez UI durumu.
 */
package com.example.ballsort.ui.game

import androidx.compose.runtime.Immutable
import com.example.ballsort.domain.model.BotDifficulty
import com.example.ballsort.domain.model.GameStatus
import com.example.ballsort.domain.model.LevelPacing
import com.example.ballsort.domain.model.PalaceTheme
import com.example.ballsort.domain.model.Tube

@Immutable
data class TransferAnimation(
    val id: Long,
    val from: Int,
    val to: Int,
    val colorId: Int,
    val count: Int,
    val targetStartSlot: Int,
)

@Immutable
data class GameUiState(
    val levelId: Int = 1,
    val isDaily: Boolean = false,
    val isDuel: Boolean = false,
    val isLanDuel: Boolean = false,
    val botDifficulty: BotDifficulty? = null,
    val botMoves: Int = 0,
    val botTotalMoves: Int = 0,
    val lanOpponentCompleted: Int = 0,
    val lanOpponentTotal: Int = 7,
    val lanOpponentMoves: Int = 0,
    val duelBotWon: Boolean = false,
    val capacity: Int = 4,
    val tubes: List<Tube> = emptyList(),
    val selectedTube: Int? = null,
    val moves: Int = 0,
    val maxMoves: Int? = null,
    val elapsedMs: Long = 0L,
    val canUndo: Boolean = false,
    val freeUndosRemaining: Int = 3,
    val undoTokens: Int = 0,
    val coins: Int = 0,
    val extraTubeTokens: Int = 0,
    val extraTubeAvailable: Boolean = true,
    val status: GameStatus = GameStatus.Playing,
    val hintMove: Pair<Int, Int>? = null,
    val stars: Int = 0,
    val isLoading: Boolean = true,
    val isSolvingHint: Boolean = false,
    val isAnimating: Boolean = false,
    val activeTransfer: TransferAnimation? = null,
    val invalidShakeTube: Int? = null,
    val invalidShakeToken: Long = 0L,
    val colorBlind: Boolean = false,
    val ballSkin: String = "classic",
    val tubeSkin: String = "glass",
    val lockedTubeIndex: Int? = null,
    val pacing: LevelPacing = PalaceTheme.getPacing(1),
    val earnedCoins: Int = 0,
    val isChestOpened: Boolean = false,
    val rematchRequestedByOpponent: Boolean = false,
    val rematchRequestedByMe: Boolean = false,
    val lanMyWins: Int = 0,
    val lanOpponentWins: Int = 0,
)
