/*
 * com.example.ballsort.domain.model
 * Seviye üretim parametrelerini tutan yapılandırma modeli.
 */
package com.example.ballsort.domain.model

data class LevelConfig(
    val levelId: Int,
    val colorCount: Int,
    val capacity: Int,
    val emptyTubes: Int,
    val seed: Long,
    val shuffleMoves: Int,
)

