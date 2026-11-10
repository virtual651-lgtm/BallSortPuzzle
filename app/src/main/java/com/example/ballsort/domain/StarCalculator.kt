/*
 * com.example.ballsort.domain
 * Hamle sayısı ve ipucu kullanımına göre 1-3 arası yıldız hesaplar.
 */
package com.example.ballsort.domain

object StarCalculator {

    fun calcStars(moves: Int, optimalMoves: Int, hintsUsed: Int): Int {
        val safeOptimal = optimalMoves.coerceAtLeast(1)
        val ratio = moves.toDouble() / safeOptimal
        var stars = when {
            ratio <= 1.2 -> 3
            ratio <= 1.5 -> 2
            else -> 1
        }
        if (hintsUsed >= 3) {
            stars = minOf(stars, 2)
        }
        return stars
    }

    /**
     * Çözücü süre aşımına uğrarsa kullanılacak gerçekçi tahmini hamle sayısı.
     */
    fun fallbackOptimalMoves(colorCount: Int, capacity: Int): Int =
        maxOf(colorCount * 3, (colorCount * capacity * 0.85).toInt())
}

