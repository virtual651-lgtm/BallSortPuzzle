/*
 * com.example.ballsort.ui.theme
 * Yıldızlarla açılan top ve tüp görsel temaları.
 */
package com.example.ballsort.ui.theme

import androidx.annotation.StringRes
import com.example.ballsort.R

enum class BallSkin(
    val id: String,
    @StringRes val nameRes: Int,
    val requiredStars: Int,
) {
    CLASSIC("classic", R.string.skin_ball_classic, 0),
    NEON("neon", R.string.skin_ball_neon, 15),
    GEM("gem", R.string.skin_ball_gem, 40),
    MINIMAL("minimal", R.string.skin_ball_minimal, 75),
    ;

    companion object {
        fun fromId(id: String): BallSkin = entries.firstOrNull { it.id == id } ?: CLASSIC
    }
}

enum class TubeSkin(
    val id: String,
    @StringRes val nameRes: Int,
    val requiredStars: Int,
) {
    GLASS("glass", R.string.skin_tube_glass, 0),
    DARK("dark", R.string.skin_tube_dark, 20),
    WOOD("wood", R.string.skin_tube_wood, 50),
    GOLD("gold", R.string.skin_tube_gold, 90),
    ;

    companion object {
        fun fromId(id: String): TubeSkin = entries.firstOrNull { it.id == id } ?: GLASS
    }
}

