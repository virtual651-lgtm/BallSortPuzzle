/*
 * com.example.ballsort.ui.menu
 * Ana menü ViewModel'i: devam edilecek seviye, günlük meydan okuma ve ücretsiz sürüm kontrolü.
 */
package com.example.ballsort.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballsort.data.AppUpdateInfo
import com.example.ballsort.data.ProgressRepository
import com.example.ballsort.data.UpdateChecker
import com.example.ballsort.util.currentEpochDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MenuUiState(
    val nextLevelId: Int = 1,
    val hasInProgress: Boolean = false,
    val isDailyCompleted: Boolean = false,
    val updateInfo: AppUpdateInfo? = null,
)

@HiltViewModel
class MenuViewModel @Inject constructor(
    progressRepository: ProgressRepository,
    private val updateChecker: UpdateChecker,
) : ViewModel() {

    private val updateFlow = MutableStateFlow<AppUpdateInfo?>(null)

    val uiState: StateFlow<MenuUiState> = combine(
        progressRepository.saveData,
        updateFlow,
    ) { save, update ->
        val today = currentEpochDay()
        val inProg = save.inProgress
        MenuUiState(
            nextLevelId = inProg?.levelId?.takeIf { it > 0 } ?: save.currentLevel,
            hasInProgress = inProg != null && inProg.dailyEpochDay == null,
            isDailyCompleted = save.lastCompletedDailyEpochDay == today,
            updateInfo = update,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = MenuUiState(),
    )

    init {
        viewModelScope.launch {
            updateFlow.value = updateChecker.checkForUpdate()
        }
    }
}

