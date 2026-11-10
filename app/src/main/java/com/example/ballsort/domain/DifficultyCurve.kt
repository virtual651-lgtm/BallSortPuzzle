/*
 * com.example.ballsort.domain
 * Seviye numarasına ve günlük tarihe göre zorluk parametrelerini hesaplar.
 */
package com.example.ballsort.domain

import com.example.ballsort.domain.model.LevelConfig

object DifficultyCurve {

    const val TOTAL_CURATED_LEVELS = 120

    fun configFor(levelId: Int): LevelConfig {
        val safeId = levelId.coerceAtLeast(1)
        val (colors, capacity) = when {
            safeId <= 5 -> 3 to 4
            safeId <= 15 -> (4 + (safeId - 6) / 5) to 4       // 4..5
            safeId <= 30 -> (6 + (safeId - 16) / 8) to 4      // 6..7
            safeId <= 50 -> (8 + (safeId - 31) / 10) to 4     // 8..9
            safeId <= 80 -> (10 + (safeId - 51) / 15) to 4    // 10..11
            else -> 12 to 4
        }
        return LevelConfig(
            levelId = safeId,
            colorCount = colors,
            capacity = capacity,
            emptyTubes = 2,
            seed = 1_000L + safeId * 7919L,
            shuffleMoves = 40 + colors * capacity * 6,
        )
    }

    /**
     * Günlük meydan okuma için tarih tabanlı deterministik yapılandırma.
     * VARSAYIM: Günlük seviyeler 7-9 renk arası orta-zor dengede üretilir.
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
            shuffleMoves = 45 + colors * capacity * 6,
        )
    }
}

