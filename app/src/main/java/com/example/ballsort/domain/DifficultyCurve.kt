/*
 * com.example.ballsort.domain
 * Seviye numarasına, saray aşamasına ve günlük tarihe göre zorluk parametrelerini hesaplar.
 * Sonsuz seviye ölçeklendirmesini ve Royal Pacing dengesini destekler.
 */
package com.example.ballsort.domain

import com.example.ballsort.domain.model.BotDifficulty
import com.example.ballsort.domain.model.LevelConfig
import com.example.ballsort.domain.model.LevelTier
import com.example.ballsort.domain.model.PalaceTheme

object DifficultyCurve {

    const val TOTAL_CURATED_LEVELS = 200

    fun configFor(levelId: Int): LevelConfig {
        val safeId = levelId.coerceAtLeast(1)
        val pacing = PalaceTheme.getPacing(safeId)

        // Standart tüp kapasitesi 4 (mobil ekran akıcılığı ve kombinatorik doğruluk için)
        val capacity = 4
        val emptyTubes = 2

        // Renk sayısı eğrisi (kademeli ve tatmin edici artış)
        val baseColors = when {
            safeId <= 3 -> 3
            safeId <= 8 -> 4
            safeId <= 15 -> 5
            safeId <= 25 -> 6
            safeId <= 45 -> 7
            safeId <= 70 -> 8
            safeId <= 100 -> 9
            safeId <= 150 -> 10
            safeId <= 200 -> 11
            else -> 12 // 200+ bölümlerde en fazla 12 renk (14 tüp)
        }

        // Pacing'e göre hafif taktiksel ayarlamalar
        val colors = when (pacing.tier) {
            LevelTier.NORMAL -> baseColors
            LevelTier.HARD -> minOf(12, baseColors + if (safeId > 15) 1 else 0)
            LevelTier.SUPER_HARD -> minOf(12, baseColors + if (safeId > 10) 1 else 0)
            LevelTier.BOSS -> baseColors // Boss düellolarında adil ve dengeli tahta
        }

        val shuffleMultiplier = when (pacing.tier) {
            LevelTier.NORMAL -> 5
            LevelTier.HARD -> 7
            LevelTier.SUPER_HARD -> 8
            LevelTier.BOSS -> 6
        }

        val shuffleMoves = 25 + colors * capacity * shuffleMultiplier
        val minSolutionMoves = when (pacing.tier) {
            LevelTier.NORMAL -> (colors * capacity * 0.4).toInt()
            LevelTier.HARD -> (colors * capacity * 0.55).toInt()
            LevelTier.SUPER_HARD -> (colors * capacity * 0.65).toInt()
            LevelTier.BOSS -> (colors * capacity * 0.5).toInt()
        }

        return LevelConfig(
            levelId = safeId,
            colorCount = colors,
            capacity = capacity,
            emptyTubes = emptyTubes,
            seed = 1_000L + safeId * 7919L,
            shuffleMoves = shuffleMoves,
            minSolutionMoves = minSolutionMoves,
            maxMoves = null,
        )
    }

    /**
     * Günlük meydan okuma için tarih tabanlı deterministik yapılandırma.
     */
    fun dailyConfig(epochDay: Long): LevelConfig {
        val colors = 7 + ((epochDay % 3L).toInt().let { if (it < 0) it + 3 else it })
        val capacity = 4
        return LevelConfig(
            levelId = -1,
            colorCount = colors,
            capacity = capacity,
            emptyTubes = 2,
            seed = 9_000_000L + epochDay * 104_729L,
            shuffleMoves = 50 + colors * capacity * 6,
            minSolutionMoves = (colors * capacity * 0.5).toInt(),
        )
    }

    /**
     * Bot düellosu veya Boss mücadelesi için seçilen zorluğa göre tahta yapılandırması.
     */
    fun duelConfig(
        seed: Long = System.currentTimeMillis(),
        difficulty: BotDifficulty = BotDifficulty.MEDIUM,
    ): LevelConfig {
        val colors = difficulty.colorCount
        val capacity = difficulty.capacity
        return LevelConfig(
            levelId = difficulty.levelId,
            colorCount = colors,
            capacity = capacity,
            emptyTubes = difficulty.emptyTubes,
            seed = seed,
            shuffleMoves = 45 + colors * capacity * 5,
            minSolutionMoves = (colors * capacity * 0.45).toInt(),
        )
    }
}
