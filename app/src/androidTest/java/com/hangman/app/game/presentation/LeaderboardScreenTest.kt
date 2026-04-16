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
                            GameResultUi(1L, "ARTURO", 1450, "SKATEBOARD", "Sports", true),
                            GameResultUi(2L, "PLAYER1", 0, "PYTHON", "Technology", false)
                        )
                    ),
                    onAction = {}
                )
            }
        }

        composeTestRule.onNodeWithText("ARTURO    ").assertIsDisplayed()
        composeTestRule.onNodeWithText("PLAYER1   ").assertIsDisplayed()
        composeTestRule.onNodeWithText("  1450").assertIsDisplayed()
        composeTestRule.onNodeWithText("     0").assertIsDisplayed()
    }
}
