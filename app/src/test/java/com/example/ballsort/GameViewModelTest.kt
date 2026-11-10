/*
 * com.example.ballsort
 * GameViewModel birim testleri (sahte repository'ler ile).
 */
package com.example.ballsort

import androidx.lifecycle.SavedStateHandle
import com.example.ballsort.data.InProgress
import com.example.ballsort.data.LevelResult
import com.example.ballsort.data.ProgressRepository
import com.example.ballsort.data.SaveData
import com.example.ballsort.data.Settings
import com.example.ballsort.data.SettingsRepository
import com.example.ballsort.domain.GameLogic
import com.example.ballsort.ui.game.GameEvent
import com.example.ballsort.ui.game.GameViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private class FakeProgressRepository : ProgressRepository {
        val state = MutableStateFlow(SaveData())
        var saveInProgressCalls = 0

        override val saveData: Flow<SaveData> = state

        override suspend fun saveResult(levelId: Int, result: LevelResult) {
            state.value = state.value.copy(
                unlockedLevel = maxOf(state.value.unlockedLevel, levelId + 1),
                levels = state.value.levels + (levelId to result),
                inProgress = null,
            )
        }

        override suspend fun saveDailyCompleted(epochDay: Long) {
            state.value = state.value.copy(lastCompletedDailyEpochDay = epochDay, inProgress = null)
        }

        override suspend fun saveInProgress(inProgress: InProgress) {
            saveInProgressCalls++
            state.value = state.value.copy(inProgress = inProgress)
        }

        override suspend fun clearInProgress() {
            state.value = state.value.copy(inProgress = null)
        }
    }

    private class FakeSettingsRepository : SettingsRepository {
        val state = MutableStateFlow(Settings())
        override val settings: Flow<Settings> = state
        override suspend fun updateSettings(transform: (Settings) -> Settings) {
            state.value = transform(state.value)
        }
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun selectMoveUndoAndAddTube_updateStateAndPersist() = runTest(testDispatcher) {
        val progressRepo = FakeProgressRepository()
        val settingsRepo = FakeSettingsRepository()
        val vm = GameViewModel(
            savedStateHandle = SavedStateHandle(mapOf("levelId" to 1)),
            progressRepository = progressRepo,
            settingsRepository = settingsRepo,
            defaultDispatcher = testDispatcher,
        )
        // Test sırasında sonsuz süre sayacının sanal zamanı döndürmemesi için ekranı duraklat
        vm.onEvent(GameEvent.ScreenPaused)
        advanceUntilIdle()

        val initialState = vm.uiState.value
        assertFalse(initialState.isLoading)
        val validMove = GameLogic.usefulMoves(initialState.tubes, initialState.capacity).first()

        // İlk tüpü seç ve iptal et
        vm.onEvent(GameEvent.TubeTapped(validMove.first))
        assertEquals(validMove.first, vm.uiState.value.selectedTube)
        vm.onEvent(GameEvent.TubeTapped(validMove.first))
        assertNull(vm.uiState.value.selectedTube)

        // Geçerli hamle yap
        vm.onEvent(GameEvent.TubeTapped(validMove.first))
        vm.onEvent(GameEvent.TubeTapped(validMove.second))
        vm.onEvent(GameEvent.AnimationFinished)
        advanceUntilIdle()

        assertEquals(1, vm.uiState.value.moves)
        assertTrue(vm.uiState.value.canUndo)
        assertNotNull(progressRepo.state.value.inProgress)
        assertTrue(progressRepo.saveInProgressCalls >= 2)

        // Geri al
        vm.onEvent(GameEvent.Undo)
        advanceUntilIdle()
        assertEquals(initialState.tubes, vm.uiState.value.tubes)
        assertFalse(vm.uiState.value.canUndo)

        // Ekstra tüp ekle
        val countBefore = vm.uiState.value.tubes.size
        vm.onEvent(GameEvent.AddTube)
        advanceUntilIdle()
        assertEquals(countBefore + 1, vm.uiState.value.tubes.size)
        assertFalse(vm.uiState.value.extraTubeAvailable)
    }
}
