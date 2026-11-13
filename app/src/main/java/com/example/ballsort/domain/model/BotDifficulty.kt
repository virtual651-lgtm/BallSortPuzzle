package com.example.ballsort.domain.model

import com.example.ballsort.R

enum class BotDifficulty(
    val levelId: Int,
    val titleRes: Int,
    val badgeRes: Int,
    val descRes: Int,
    val delayMs: Long,
    val moveMultiplier: Float,
    val colorCount: Int,
    val capacity: Int = 4,
    val emptyTubes: Int = 2,
) {
    EASY(
        levelId = -201,
        titleRes = R.string.bot_diff_easy_title,
        badgeRes = R.string.bot_diff_easy_badge,
        descRes = R.string.bot_diff_easy_desc,
        delayMs = 1_800L,
        moveMultiplier = 1.35f,
        colorCount = 5,
    ),
    MEDIUM(
        levelId = -2,
        titleRes = R.string.bot_diff_medium_title,
        badgeRes = R.string.bot_diff_medium_badge,
        descRes = R.string.bot_diff_medium_desc,
        delayMs = 1_200L,
        moveMultiplier = 1.15f,
        colorCount = 6,
    ),
    HARD(
        levelId = -202,
        titleRes = R.string.bot_diff_hard_title,
        badgeRes = R.string.bot_diff_hard_badge,
        descRes = R.string.bot_diff_hard_desc,
        delayMs = 850L,
        moveMultiplier = 1.0f,
        colorCount = 7,
    );

    companion object {
        fun isBotDuel(levelId: Int): Boolean = levelId == -2 || levelId == -201 || levelId == -202

        fun fromLevelId(levelId: Int): BotDifficulty = when (levelId) {
            -201 -> EASY
            -202 -> HARD
            else -> MEDIUM
        }
    }
}

