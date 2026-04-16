package com.hangman.app.game.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import com.hangman.app.game.presentation.game_play.GamePlayState
import com.hangman.app.game.presentation.game_play.GameStatus
import org.junit.Rule
import org.junit.Test

class GamePlayScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val robot by lazy { GamePlayRobot(composeTestRule) }

    @Test
    fun keyboard_shows_all_26_letters_initially() {
        robot.setContent(
            state = GamePlayState(
                word = "HANGMAN",
                category = "Classic",
                displayWord = "_ _ _ _ _ _ _",
                guessedLetters = emptySet(),
                wrongGuesses = 0,
                maxAttempts = 6,
                gameStatus = GameStatus.Playing
            )
        )

        ('A'..'Z').forEach { letter ->
            robot.assertLetterButtonDisplayed(letter.toString())
        }
    }

    @Test
    fun guessed_letters_are_disabled_on_keyboard() {
        robot.setContent(
            state = GamePlayState(
                word = "HANGMAN",
                category = "Classic",
                displayWord = "H _ _ _ _ _ _",
                guessedLetters = setOf('H', 'X'),
                wrongGuesses = 1,
                maxAttempts = 6,
                gameStatus = GameStatus.Playing
            )
        )

        robot.assertLetterButtonDisabled("H")
        robot.assertLetterButtonDisabled("X")
        robot.assertLetterButtonEnabled("A")
    }

    @Test
    fun won_state_shows_win_banner_and_play_again() {
        robot.setContent(
            state = GamePlayState(
                word = "CAT",
                category = "Animals",
                displayWord = "C A T",
                guessedLetters = setOf('C', 'A', 'T'),
                wrongGuesses = 0,
                maxAttempts = 6,
                gameStatus = GameStatus.Won
            )
        )

        robot
            .assertWonBannerVisible()
            .assertPlayAgainButtonVisible()
    }

    @Test
    fun lost_state_shows_game_over_banner_and_reveals_word() {
        robot.setContent(
            state = GamePlayState(
                word = "ELEPHANT",
                category = "Animals",
                displayWord = "_ _ _ _ _ _ _ _",
                guessedLetters = setOf('X', 'Z', 'Q', 'J', 'V', 'W'),
                wrongGuesses = 6,
                maxAttempts = 6,
                gameStatus = GameStatus.Lost
            )
        )

        robot
            .assertLostBannerVisible()
            .assertWordRevealed("ELEPHANT")
            .assertPlayAgainButtonVisible()
    }
}
