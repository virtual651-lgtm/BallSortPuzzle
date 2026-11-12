/*
 * com.example.ballsort.data
 * DataStore + kotlinx.serialization tabanlı ilerleme ve ayar depoları.
 */
package com.example.ballsort.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
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
            try {
                json.decodeFromString<SaveData>(raw)
            } catch (_: Exception) {
                SaveData()
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
            val nextUnlocked = maxOf(current.unlockedLevel, levelId + 1)
            val updated = current.copy(
                currentLevel = maxOf(current.currentLevel, levelId + 1),
                unlockedLevel = nextUnlocked,
                levels = current.levels + (levelId to merged),
                inProgress = null,
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
            val updated = current.copy(
                lastCompletedDailyEpochDay = epochDay,
                dailyStreak = newStreak,
                bestDailyStreak = maxOf(current.bestDailyStreak, newStreak),
                totalDailyCompleted = if (alreadyCompletedToday) current.totalDailyCompleted else current.totalDailyCompleted + 1,
                inProgress = null,
            )
            prefs[saveKey] = json.encodeToString(updated)
        }
    }

    override suspend fun recordDuelWin() {
        dataStore.edit { prefs ->
            val current = decodeSave(prefs[saveKey])
            prefs[saveKey] = json.encodeToString(current.copy(duelWins = current.duelWins + 1))
        }
    }

    override suspend fun recordNoHintWin() {
        dataStore.edit { prefs ->
            val current = decodeSave(prefs[saveKey])
            prefs[saveKey] = json.encodeToString(current.copy(noHintWins = current.noHintWins + 1))
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

    private fun decodeSave(raw: String?): SaveData {
        if (raw == null) return SaveData()
        return try {
            json.decodeFromString<SaveData>(raw)
        } catch (_: Exception) {
            SaveData()
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

