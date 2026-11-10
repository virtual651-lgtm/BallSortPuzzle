/*
 * com.example.ballsort.ui.levels
 * Seviye seçim ekranı ViewModel'i: açık seviyeler, yıldızlar ve en iyi hamle sayıları.
 */
package com.example.ballsort.ui.levels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballsort.data.LevelResult
import com.example.ballsort.data.ProgressRepository
import com.example.ballsort.domain.DifficultyCurve
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class LevelSelectUiState(
    val unlockedLevel: Int = 1,
    val totalLevels: Int = DifficultyCurve.TOTAL_CURATED_LEVELS,
    val results: Map<Int, LevelResult> = emptyMap(),
)

@HiltViewModel
class LevelSelectViewModel @Inject constructor(
    progressRepository: ProgressRepository,
) : ViewModel() {

    val uiState: StateFlow<LevelSelectUiState> = progressRepository.saveData
        .map { save ->
            LevelSelectUiState(
                unlockedLevel = save.unlockedLevel,
                totalLevels = maxOf(DifficultyCurve.TOTAL_CURATED_LEVELS, save.unlockedLevel),
                results = save.levels,
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = LevelSelectUiState(),
        )
}

