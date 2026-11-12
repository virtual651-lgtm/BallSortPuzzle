/*
 * com.example.ballsort.data
 * Oyun ilerlemesi ve ayarlar için repository arayüzleri.
 */
package com.example.ballsort.data

import kotlinx.coroutines.flow.Flow

interface ProgressRepository {
    val saveData: Flow<SaveData>
    suspend fun saveResult(levelId: Int, result: LevelResult)
    suspend fun saveDailyCompleted(epochDay: Long)
    suspend fun recordDuelWin() {}
    suspend fun recordNoHintWin() {}
    suspend fun saveInProgress(inProgress: InProgress)
    suspend fun clearInProgress()
    suspend fun setWalkthroughSeen() {}
}

interface SettingsRepository {
    val settings: Flow<Settings>
    suspend fun updateSettings(transform: (Settings) -> Settings)
}

