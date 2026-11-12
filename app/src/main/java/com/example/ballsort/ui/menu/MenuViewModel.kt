/*
 * com.example.ballsort.ui.menu
 * Ana menü ViewModel'i: devam edilecek seviye, günlük meydan okuma ve ücretsiz sürüm kontrolü.
 */
package com.example.ballsort.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ballsort.data.AchievementItem
import com.example.ballsort.data.Achievements
import com.example.ballsort.data.AppUpdateInfo
import com.example.ballsort.data.ProgressRepository
import com.example.ballsort.data.UpdateChecker
import com.example.ballsort.platform.AppUpdateInstaller
import com.example.ballsort.platform.LanDuelState
import com.example.ballsort.platform.LanDuelTransport
import com.example.ballsort.platform.UpdateDownloadState
import com.example.ballsort.util.currentEpochDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class MenuUiState(
    val nextLevelId: Int = 1,
    val hasInProgress: Boolean = false,
    val isDailyCompleted: Boolean = false,
    val totalStars: Int = 0,
    val completedLevels: Int = 0,
    val dailyStreak: Int = 0,
    val achievements: List<AchievementItem> = emptyList(),
    val updateInfo: AppUpdateInfo? = null,
    val shouldShowWalkthrough: Boolean = false,
)

@HiltViewModel
class MenuViewModel @Inject constructor(
    private val progressRepository: ProgressRepository,
    private val updateChecker: UpdateChecker,
    private val appUpdateInstaller: AppUpdateInstaller,
    private val lanDuelTransport: LanDuelTransport,
) : ViewModel() {

    val lanDuelState: StateFlow<LanDuelState> = lanDuelTransport.state

    private val updateFlow = MutableStateFlow<AppUpdateInfo?>(null)
    private val _downloadState = MutableStateFlow<UpdateDownloadState>(UpdateDownloadState.Idle)
    val downloadState: StateFlow<UpdateDownloadState> = _downloadState.asStateFlow()

    val uiState: StateFlow<MenuUiState> = combine(
        progressRepository.saveData,
        updateFlow,
    ) { save, update ->
        val today = currentEpochDay()
        val inProg = save.inProgress
        val activeStreak = if (
            save.lastCompletedDailyEpochDay == today ||
            save.lastCompletedDailyEpochDay == today - 1L
        ) {
            save.dailyStreak
        } else {
            0
        }
        MenuUiState(
            nextLevelId = inProg?.levelId?.takeIf { it > 0 } ?: save.currentLevel,
            hasInProgress = inProg != null && inProg.dailyEpochDay == null,
            isDailyCompleted = save.lastCompletedDailyEpochDay == today,
            totalStars = save.levels.values.sumOf { it.stars },
            completedLevels = save.levels.size,
            dailyStreak = activeStreak,
            achievements = Achievements.evaluate(save),
            updateInfo = update,
            shouldShowWalkthrough = !save.hasSeenWalkthrough && save.levels.isEmpty(),
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

    fun hostLanDuel() = lanDuelTransport.startHosting()
    fun browseLanDuel() = lanDuelTransport.startBrowsing()
    fun connectLanDuel(host: String, port: Int) = lanDuelTransport.connectTo(host, port)
    fun closeLanDuel() = lanDuelTransport.close()

    fun dismissInitialWalkthrough() {
        viewModelScope.launch {
            progressRepository.setWalkthroughSeen()
        }
    }

    fun downloadAndInstallUpdate(context: android.content.Context, apkUrl: String) {
        viewModelScope.launch {
            appUpdateInstaller.downloadApk(context.applicationContext, apkUrl).collect { state ->
                _downloadState.value = state
                if (state is UpdateDownloadState.ReadyToInstall) {
                    appUpdateInstaller.installApk(context, state.file)
                }
            }
        }
    }

    fun installApkFile(context: android.content.Context, file: File) {
        appUpdateInstaller.installApk(context, file)
    }
}

