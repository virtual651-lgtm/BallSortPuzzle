/*
 * com.example.ballsort.data
 * Oyun ilerlemesi, ekonomi ve ayarlar için repository arayüzleri.
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

    // Yeni Ekonomi & Ödül Sistemi
    suspend fun addCoins(amount: Int) {}
    suspend fun spendCoins(amount: Int): Boolean = false
    suspend fun useUndoToken(): Boolean = false
    suspend fun addUndoTokens(amount: Int) {}
    suspend fun useExtraTubeToken(): Boolean = false
    suspend fun addExtraTubeTokens(amount: Int) {}
    suspend fun unlockSkin(skinName: String) {}
}

interface SettingsRepository {
    val settings: Flow<Settings>
    suspend fun updateSettings(transform: (Settings) -> Settings)
}
