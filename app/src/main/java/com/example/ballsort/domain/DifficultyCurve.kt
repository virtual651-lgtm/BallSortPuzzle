/*
 * com.example.ballsort.domain
 * Seviye numarasına ve günlük tarihe göre zorluk parametrelerini hesaplar.
 */
package com.example.ballsort.domain

import com.example.ballsort.domain.model.LevelConfig

object DifficultyCurve {

    const val TOTAL_CURATED_LEVELS = 200

    fun configFor(levelId: Int): LevelConfig {
        val safeId = levelId.coerceAtLeast(1)
        // İlk 30 bölüm başlangıç dengesi için sabit tutulur
        if (safeId <= 30) {
            val (colors, capacity) = when {
                safeId <= 5 -> 3 to 4
                safeId <= 15 -> (4 + (safeId - 6) / 5) to 4       // 4..5
                else -> (6 + (safeId - 16) / 8) to 4              // 6..7
            }
            return LevelConfig(
                levelId = safeId,
                colorCount = colors,
                capacity = capacity,
                emptyTubes = 2,
                seed = 1_000L + safeId * 7919L,
                shuffleMoves = 40 + colors * capacity * 6,
                minSolutionMoves = 0,
            )
        }

        // 31+ bölümler: her 5. bölüm tek boş tüplü taktiksel zorluk, 61+ itibarıyla kapasite 5
        val isChallengeLevel = safeId % 5 == 0
        val capacity = if (safeId >= 61 && !isChallengeLevel) 5 else 4
        val baseColors = when {
            safeId <= 50 -> 8 + (safeId - 31) / 10                // 8..9
            safeId <= 80 -> 10 + (safeId - 51) / 15               // 10..11
            safeId <= 140 -> 12
            safeId <= 170 -> 13
            else -> 14
        }
        val emptyTubes = if (isChallengeLevel) 1 else 2
        val colors = if (isChallengeLevel) {
            when {
                safeId < 60 -> 5
                safeId < 120 -> 6
                else -> 7
            }
        } else {
            baseColors
        }

        val maxMoves = if (safeId >= 40 && safeId % 10 == 0) {
            (colors * capacity * 1.4).toInt()
        } else {
            null
        }

        return LevelConfig(
            levelId = safeId,
            colorCount = colors,
            capacity = capacity,
            emptyTubes = emptyTubes,
            seed = 1_000L + safeId * 7919L,
            shuffleMoves = 60 + colors * capacity * 8,
            minSolutionMoves = (colors * capacity * 0.65).toInt(),
            maxMoves = maxMoves,
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
            shuffleMoves = 55 + colors * capacity * 7,
            minSolutionMoves = (colors * capacity * 0.6).toInt(),
        )
    }

    /**
     * Bot düellosu için orta-zor dengede yeni tahta yapılandırması.
     */
    fun duelConfig(seed: Long = System.currentTimeMillis()): LevelConfig {
        val colors = 7
        val capacity = 4
        return LevelConfig(
            levelId = -2,
            colorCount = colors,
            capacity = capacity,
            emptyTubes = 2,
            seed = seed,
            shuffleMoves = 60 + colors * capacity * 7,
            minSolutionMoves = (colors * capacity * 0.6).toInt(),
        )
    }
}

