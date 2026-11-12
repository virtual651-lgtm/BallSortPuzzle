/*
 * com.example.ballsort.data
 * Oyuncu başarımları (rozetler) ve kilit açma koşulları.
 */
package com.example.ballsort.data

import androidx.annotation.StringRes
import com.example.ballsort.R

data class AchievementItem(
    val id: String,
    @StringRes val titleRes: Int,
    @StringRes val descRes: Int,
    val unlocked: Boolean,
)

object Achievements {
    fun evaluate(save: SaveData): List<AchievementItem> {
        val totalStars = save.levels.values.sumOf { it.stars }
        return listOf(
            AchievementItem(
                id = "first_win",
                titleRes = R.string.ach_first_win_title,
                descRes = R.string.ach_first_win_desc,
                unlocked = save.levels.isNotEmpty(),
            ),
            AchievementItem(
                id = "duel_winner",
                titleRes = R.string.ach_duel_winner_title,
                descRes = R.string.ach_duel_winner_desc,
                unlocked = save.duelWins >= 1,
            ),
            AchievementItem(
                id = "streak_3",
                titleRes = R.string.ach_streak_3_title,
                descRes = R.string.ach_streak_3_desc,
                unlocked = save.bestDailyStreak >= 3,
            ),
            AchievementItem(
                id = "no_hint_5",
                titleRes = R.string.ach_no_hint_5_title,
                descRes = R.string.ach_no_hint_5_desc,
                unlocked = save.noHintWins >= 5,
            ),
            AchievementItem(
                id = "stars_30",
                titleRes = R.string.ach_stars_30_title,
                descRes = R.string.ach_stars_30_desc,
                unlocked = totalStars >= 30,
            ),
        )
    }
}

