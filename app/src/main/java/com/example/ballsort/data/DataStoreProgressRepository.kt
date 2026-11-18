/*
 * com.example.ballsort.data
 * DataStore + kotlinx.serialization tabanlı ilerleme, ekonomi ve ayar depoları.
 * Geriye dönük uyumludur, oyuncu ilerlemesini ve geçmiş başarılarını korur.
 */
package com.example.ballsort.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.ballsort.domain.model.LevelTier
import com.example.ballsort.domain.model.PalaceTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

val Context.ballSortDataStore: DataStore<Preferences> by preferencesDataStore(name = "ball_sort_prefs")

@Singleton
class DataStoreProgressRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json,
) : ProgressRepository {

    private val saveKey = stringPreferencesKey("save_data_json")

    override val saveData: Flow<SaveData> = dataStore.data
        .catch { e ->
            if (e is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw e
        }
        .map { prefs ->
            val raw = prefs[saveKey] ?: return@map SaveData()
            val parsed = try {
                json.decodeFromString<SaveData>(raw)
            } catch (_: Exception) {
                SaveData()
            }

            // Geriye dönük hoş geldin bonusu (Eski oyunculardan veri kaybı olmasın, hakları verilsin)
            if (!parsed.hasMigratedRetroactiveCoins && parsed.unlockedLevel > 1) {
                val retroactiveBonus = (parsed.unlockedLevel - 1) * 20
                parsed.copy(
                    coins = parsed.coins + retroactiveBonus,
                    hasMigratedRetroactiveCoins = true,
                )
            } else {
                parsed
            }
        }

    override suspend fun saveResult(levelId: Int, result: LevelResult) {
        dataStore.edit { prefs ->
            val current = decodeSave(prefs[saveKey])
            val prev = current.levels[levelId]
            val merged = if (prev == null) {
                result
            } else {
                LevelResult(
                    stars = maxOf(prev.stars, result.stars),
                    bestMoves = minOf(prev.bestMoves, result.bestMoves),
                    bestTimeMs = minOf(prev.bestTimeMs, result.bestTimeMs),
                )
            }

            // Pacing ve Seviye Ödülü Hesaplama
            val pacing = PalaceTheme.getPacing(levelId)
            val baseReward = when (result.stars) {
                3 -> 30
                2 -> 20
                else -> 10
            }
            val levelCoins = baseReward * pacing.coinMultiplier

            // Boss seviyelerinde ekstra Hazine Sandığı ödülü
            val isBoss = pacing.tier == LevelTier.BOSS
            val chestCoins = if (isBoss && prev == null) 150 else 0
            val chestTubeTokens = if (isBoss && prev == null) 1 else 0
            val chestUndoTokens = if (isBoss && prev == null) 2 else 0

            val nextUnlocked = maxOf(current.unlockedLevel, levelId + 1)
            val updated = current.copy(
                currentLevel = maxOf(current.currentLevel, levelId + 1),
                unlockedLevel = nextUnlocked,
                levels = current.levels + (levelId to merged),
                coins = current.coins + levelCoins + chestCoins,
                extraTubeTokens = current.extraTubeTokens + chestTubeTokens,
                undoTokens = current.undoTokens + chestUndoTokens,
                inProgress = null,
                hasMigratedRetroactiveCoins = true,
            )
            prefs[saveKey] = json.encodeToString(updated)
        }
    }

    override suspend fun saveDailyCompleted(epochDay: Long) {
        dataStore.edit { prefs ->
            val current = decodeSave(prefs[saveKey])
            val alreadyCompletedToday = current.lastCompletedDailyEpochDay == epochDay
            val newStreak = when {
                alreadyCompletedToday -> current.dailyStreak
                current.lastCompletedDailyEpochDay == epochDay - 1L -> current.dailyStreak + 1
                else -> 1
            }
            val dailyReward = if (alreadyCompletedToday) 20 else (80 + newStreak * 10)
            val updated = current.copy(
                lastCompletedDailyEpochDay = epochDay,
                dailyStreak = newStreak,
                bestDailyStreak = maxOf(current.bestDailyStreak, newStreak),
                totalDailyCompleted = if (alreadyCompletedToday) current.totalDailyCompleted else current.totalDailyCompleted + 1,
                coins = current.coins + dailyReward,
                inProgress = null,
            )
            prefs[saveKey] = json.encodeToString(updated)
        }
    }

    override suspend fun recordDuelWin() {
        dataStore.edit { prefs ->
            val current = decodeSave(prefs[saveKey])
            prefs[saveKey] = json.encodeToString(
                current.copy(
                    duelWins = current.duelWins + 1,
                    coins = current.coins + 50, // Düello zafer ödülü
                )
            )
        }
    }

    override suspend fun recordNoHintWin() {
        dataStore.edit { prefs ->
            val current = decodeSave(prefs[saveKey])
            prefs[saveKey] = json.encodeToString(
                current.copy(
                    noHintWins = current.noHintWins + 1,
                    coins = current.coins + 15,
                )
            )
        }
    }

    override suspend fun addCoins(amount: Int) {
        dataStore.edit { prefs ->
            val current = decodeSave(prefs[saveKey])
            prefs[saveKey] = json.encodeToString(current.copy(coins = current.coins + amount))
        }
    }

    override suspend fun spendCoins(amount: Int): Boolean {
        var success = false
        dataStore.edit { prefs ->
            val current = decodeSave(prefs[saveKey])
            if (current.coins >= amount) {
                prefs[saveKey] = json.encodeToString(current.copy(coins = current.coins - amount))
                success = true
            }
        }
        return success
    }

    override suspend fun useUndoToken(): Boolean {
        var success = false
        dataStore.edit { prefs ->
            val current = decodeSave(prefs[saveKey])
            if (current.undoTokens > 0) {
                prefs[saveKey] = json.encodeToString(current.copy(undoTokens = current.undoTokens - 1))
                success = true
            }
        }
        return success
    }

    override suspend fun addUndoTokens(amount: Int) {
        dataStore.edit { prefs ->
            val current = decodeSave(prefs[saveKey])
            prefs[saveKey] = json.encodeToString(current.copy(undoTokens = current.undoTokens + amount))
        }
    }

    override suspend fun useExtraTubeToken(): Boolean {
        var success = false
        dataStore.edit { prefs ->
            val current = decodeSave(prefs[saveKey])
            if (current.extraTubeTokens > 0) {
                prefs[saveKey] = json.encodeToString(current.copy(extraTubeTokens = current.extraTubeTokens - 1))
                success = true
            }
        }
        return success
    }

    override suspend fun addExtraTubeTokens(amount: Int) {
        dataStore.edit { prefs ->
            val current = decodeSave(prefs[saveKey])
            prefs[saveKey] = json.encodeToString(current.copy(extraTubeTokens = current.extraTubeTokens + amount))
        }
    }

    override suspend fun unlockSkin(skinName: String) {
        dataStore.edit { prefs ->
            val current = decodeSave(prefs[saveKey])
            if (!current.unlockedSkins.contains(skinName)) {
                prefs[saveKey] = json.encodeToString(current.copy(unlockedSkins = current.unlockedSkins + skinName))
            }
        }
    }

    override suspend fun saveInProgress(inProgress: InProgress) {
        dataStore.edit { prefs ->
            val current = decodeSave(prefs[saveKey])
            val updated = current.copy(
                currentLevel = if (inProgress.levelId > 0) inProgress.levelId else current.currentLevel,
                inProgress = inProgress,
            )
            prefs[saveKey] = json.encodeToString(updated)
        }
    }

    override suspend fun clearInProgress() {
        dataStore.edit { prefs ->
            val current = decodeSave(prefs[saveKey])
            prefs[saveKey] = json.encodeToString(current.copy(inProgress = null))
        }
    }

    override suspend fun setWalkthroughSeen() {
        dataStore.edit { prefs ->
            val current = decodeSave(prefs[saveKey])
            prefs[saveKey] = json.encodeToString(current.copy(hasSeenWalkthrough = true))
        }
    }

    private fun decodeSave(raw: String?): SaveData {
        if (raw == null) return SaveData()
        val parsed = try {
            json.decodeFromString<SaveData>(raw)
        } catch (_: Exception) {
            SaveData()
        }
        return if (!parsed.hasMigratedRetroactiveCoins && parsed.unlockedLevel > 1) {
            val retroactiveBonus = (parsed.unlockedLevel - 1) * 20
            parsed.copy(
                coins = parsed.coins + retroactiveBonus,
                hasMigratedRetroactiveCoins = true,
            )
        } else {
            parsed
        }
    }
}

@Singleton
class DataStoreSettingsRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val json: Json,
) : SettingsRepository {

    private val settingsKey = stringPreferencesKey("settings_json")

    override val settings: Flow<Settings> = dataStore.data
        .catch { e ->
            if (e is IOException) emit(androidx.datastore.preferences.core.emptyPreferences()) else throw e
        }
        .map { prefs ->
            val raw = prefs[settingsKey] ?: return@map Settings()
            try {
                json.decodeFromString<Settings>(raw)
            } catch (_: Exception) {
                Settings()
            }
        }

    override suspend fun updateSettings(transform: (Settings) -> Settings) {
        dataStore.edit { prefs ->
            val current = prefs[settingsKey]?.let {
                try {
                    json.decodeFromString<Settings>(it)
                } catch (_: Exception) {
                    Settings()
                }
            } ?: Settings()
            prefs[settingsKey] = json.encodeToString(transform(current))
        }
    }
}
