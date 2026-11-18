/*
 * com.example.ballsort.data
 * Kalıcı kayıt, ayarlar, ekonomi (altın/jeton) ve ücretsiz güncelleme kontrolü için modeller.
 * %100 geriye dönük uyumludur, veri kaybı yaşatmaz.
 */
package com.example.ballsort.data

import kotlinx.serialization.Serializable

@Serializable
data class SaveData(
    val version: Int = 2,
    val currentLevel: Int = 1,
    val unlockedLevel: Int = 1,
    val levels: Map<Int, LevelResult> = emptyMap(),
    val inProgress: InProgress? = null,
    val lastCompletedDailyEpochDay: Long? = null,
    val dailyStreak: Int = 0,
    val bestDailyStreak: Int = 0,
    val totalDailyCompleted: Int = 0,
    val duelWins: Int = 0,
    val noHintWins: Int = 0,
    val hasSeenWalkthrough: Boolean = false,

    // Yeni Ekonomi ve Ödül Alanları (Varsayılan değerlerle eski kayıtları bozmaz)
    val coins: Int = 200,
    val extraTubeTokens: Int = 2,
    val undoTokens: Int = 5,
    val swapTokens: Int = 2,
    val hasMigratedRetroactiveCoins: Boolean = false,
    val unlockedSkins: List<String> = listOf("classic", "glass"),
)

@Serializable
data class LevelResult(
    val stars: Int,
    val bestMoves: Int,
    val bestTimeMs: Long,
)

@Serializable
data class InProgress(
    val levelId: Int,
    val tubes: List<List<Int>>,
    val moves: Int,
    val elapsedMs: Long,
    val history: List<MoveDto>,
    val extraTubeUsed: Boolean,
    val hintsUsed: Int = 0,
    val dailyEpochDay: Long? = null,
    val freeUndosRemaining: Int = 3,
)

@Serializable
data class MoveDto(
    val from: Int,
    val to: Int,
    val count: Int,
)

@Serializable
data class Settings(
    val sound: Boolean = true,
    val vibration: Boolean = true,
    val theme: String = "system",       // "light" | "dark" | "system"
    val dynamicColor: Boolean = true,
    val colorBlind: Boolean = false,
    val multiMove: Boolean = true,
    val language: String = "system",    // "system" | "tr" | "en"
    val ballSkin: String = "classic",   // "classic" | "neon" | "gem" | "minimal"
    val tubeSkin: String = "glass",     // "glass" | "dark" | "wood" | "gold"
)

@Serializable
data class AppUpdateInfo(
    val latestVersionCode: Int,
    val versionName: String,
    val apkUrl: String,
    val notes: String = "",
)
