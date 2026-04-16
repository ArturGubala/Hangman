package com.hangman.app.game.presentation

import androidx.compose.ui.test.ComposeContentTestRule
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.hangman.app.core.presentation.designsystem.theme.HangmanTheme
import com.hangman.app.game.presentation.game_play.GamePlayAction
import com.hangman.app.game.presentation.game_play.GamePlayScreen
import com.hangman.app.game.presentation.game_play.GamePlayState
import com.hangman.app.game.presentation.game_play.GameStatus

class GamePlayRobot(private val composeTestRule: ComposeContentTestRule) {

    fun setContent(
        state: GamePlayState,
        onAction: (GamePlayAction) -> Unit = {}
    ) = apply {
        composeTestRule.setContent {
            HangmanTheme {
                GamePlayScreen(state = state, onAction = onAction)
            }
        }
    }

    fun assertLetterButtonDisplayed(letter: String) = apply {
        composeTestRule.onNodeWithContentDescription("Letter $letter").assertIsDisplayed()
    }

    fun assertLetterButtonEnabled(letter: String) = apply {
        composeTestRule.onNodeWithContentDescription("Letter $letter").assertIsEnabled()
    }

    fun assertLetterButtonDisabled(letter: String) = apply {
        composeTestRule.onNodeWithContentDescription("Letter $letter").assertIsNotEnabled()
    }

    fun clickLetter(letter: String) = apply {
        composeTestRule.onNodeWithContentDescription("Letter $letter").performClick()
    }

    fun assertWonBannerVisible() = apply {
        composeTestRule.onNodeWithText("You Won!").assertIsDisplayed()
    }

    fun assertLostBannerVisible() = apply {
        composeTestRule.onNodeWithText("Game Over").assertIsDisplayed()
    }

    fun assertWordRevealed(word: String) = apply {
        composeTestRule.onNodeWithText("The word was: $word").assertIsDisplayed()
    }

    fun assertPlayAgainButtonVisible() = apply {
        composeTestRule.onNodeWithText("Play Again").assertIsDisplayed()
    }
}
