package com.hangman.app.game.presentation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.hangman.app.core.presentation.designsystem.theme.HangmanTheme
import com.hangman.app.game.presentation.leaderboard.GameResultUi
import com.hangman.app.game.presentation.leaderboard.LeaderboardScreen
import com.hangman.app.game.presentation.leaderboard.LeaderboardState
import com.hangman.app.game.presentation.leaderboard.TAG_EMPTY_STATE
import org.junit.Rule
import org.junit.Test

class LeaderboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun shows_empty_state_when_no_results() {
        composeTestRule.setContent {
            HangmanTheme {
                LeaderboardScreen(
                    state = LeaderboardState(results = emptyList()),
                    onAction = {}
                )
            }
        }

        composeTestRule.onNodeWithTag(TAG_EMPTY_STATE).assertIsDisplayed()
    }

    @Test
    fun displays_each_result_row() {
        composeTestRule.setContent {
            HangmanTheme {
                LeaderboardScreen(
                    state = LeaderboardState(
                        results = listOf(
                            GameResultUi(1L, "ELEPHANT", "Animals", 2, 6, true, "Apr 16, 2026 · 10:30"),
                            GameResultUi(2L, "PYTHON", "Technology", 6, 6, false, "Apr 15, 2026 · 18:00")
                        )
                    ),
                    onAction = {}
                )
            }
        }

        composeTestRule.onNodeWithText("ELEPHANT").assertIsDisplayed()
        composeTestRule.onNodeWithText("PYTHON").assertIsDisplayed()
        composeTestRule.onNodeWithText("Won").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lost").assertIsDisplayed()
    }
}
