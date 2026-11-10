/*
 * com.example.ballsort.ui.game
 * Oyun ekranının tek yönlü veri akışı (UDF) için değişmez UI durumu.
 */
package com.example.ballsort.ui.game

import androidx.compose.runtime.Immutable
import com.example.ballsort.domain.model.GameStatus
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
    val capacity: Int = 4,
    val tubes: List<Tube> = emptyList(),
    val selectedTube: Int? = null,
    val moves: Int = 0,
    val elapsedMs: Long = 0L,
    val canUndo: Boolean = false,
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
)

