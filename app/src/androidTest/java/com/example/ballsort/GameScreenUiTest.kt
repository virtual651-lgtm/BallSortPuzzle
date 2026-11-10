/*
 * com.example.ballsort
 * Compose UI enstrümantasyon testleri.
 */
package com.example.ballsort

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.platform.app.InstrumentationRegistry
import com.example.ballsort.domain.model.GameStatus
import com.example.ballsort.ui.game.GameScreenContent
import com.example.ballsort.ui.game.GameUiState
import com.example.ballsort.ui.theme.BallSortTheme
import org.junit.Rule
import org.junit.Test

class GameScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun winDialog_isDisplayedWhenStatusIsWon() {
        composeRule.setContent {
            BallSortTheme {
                GameScreenContent(
                    uiState = GameUiState(
                        levelId = 3,
                        tubes = listOf(
                            listOf(0, 0, 0, 0),
                            listOf(1, 1, 1, 1),
                            emptyList(),
                        ),
                        moves = 7,
                        elapsedMs = 18_000L,
                        status = GameStatus.Won,
                        stars = 3,
                        isLoading = false,
                    ),
                    onEvent = {},
                    onBackClick = {},
                    onNavigateMenu = {},
                )
            }
        }

        val winTitle = InstrumentationRegistry.getInstrumentation()
            .targetContext
            .getString(R.string.win_title)
        composeRule.onNodeWithText(winTitle).assertIsDisplayed()
    }
}
