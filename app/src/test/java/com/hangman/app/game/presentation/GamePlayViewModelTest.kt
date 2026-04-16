package com.hangman.app.game.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import assertk.assertions.isTrue
import com.hangman.app.game.domain.GameEngine
import com.hangman.app.game.presentation.game_play.GamePlayAction
import com.hangman.app.game.presentation.game_play.GamePlayEvent
import com.hangman.app.game.presentation.game_play.GamePlayViewModel
import com.hangman.app.game.presentation.game_play.GameStatus
import com.hangman.app.game.presentation.game_play.MAX_NICKNAME_LENGTH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GamePlayViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeRepository: FakeLeaderboardRepository
    private lateinit var gameEngine: GameEngine
    private lateinit var viewModel: GamePlayViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeLeaderboardRepository()
        gameEngine = GameEngine()
        viewModel = GamePlayViewModel(gameEngine, fakeRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state starts new game with valid word`() = runTest {
        val state = viewModel.state.value
        assertThat(state.word).isNotEmpty()
        assertThat(state.category).isNotEmpty()
        assertThat(state.gameStatus).isEqualTo(GameStatus.Playing)
        assertThat(state.wrongGuesses).isEqualTo(0)
        assertThat(state.guessedLetters.isEmpty()).isTrue()
    }

    @Test
    fun `guessing correct letter updates displayWord`() = runTest {
        val word = viewModel.state.value.word
        val correctLetter = word.first()

        viewModel.onAction(GamePlayAction.OnLetterClick(correctLetter))

        val state = viewModel.state.value
        assertThat(state.guessedLetters).contains(correctLetter)
        assertThat(state.displayWord).contains(correctLetter.toString())
    }

    @Test
    fun `guessing wrong letter increments wrongGuesses`() = runTest {
        val word = viewModel.state.value.word
        val wrongLetter = ('A'..'Z').first { it !in word }

        viewModel.onAction(GamePlayAction.OnLetterClick(wrongLetter))

        assertThat(viewModel.state.value.wrongGuesses).isEqualTo(1)
    }

    @Test
    fun `guessing same letter twice does not change state`() = runTest {
        val word = viewModel.state.value.word
        val letter = word.first()

        viewModel.onAction(GamePlayAction.OnLetterClick(letter))
        val stateAfterFirst = viewModel.state.value

        viewModel.onAction(GamePlayAction.OnLetterClick(letter))
        val stateAfterSecond = viewModel.state.value

        assertThat(stateAfterFirst).isEqualTo(stateAfterSecond)
    }

    @Test
    fun `reaching maxAttempts shows nickname dialog with zero score`() = runTest {
        val word = viewModel.state.value.word
        val wrongLetters = ('A'..'Z').filter { it !in word }.take(6)

        wrongLetters.forEach { letter ->
            if (viewModel.state.value.gameStatus == GameStatus.Playing) {
                viewModel.onAction(GamePlayAction.OnLetterClick(letter))
            }
        }

        val state = viewModel.state.value
        assertThat(state.gameStatus).isEqualTo(GameStatus.Lost)
        assertThat(state.showNicknameDialog).isTrue()
        assertThat(state.pendingScore).isEqualTo(0)
    }

    @Test
    fun `confirming nickname saves Lost result`() = runTest {
        val word = viewModel.state.value.word
        val wrongLetters = ('A'..'Z').filter { it !in word }.take(6)
        wrongLetters.forEach { letter ->
            if (viewModel.state.value.gameStatus == GameStatus.Playing) {
                viewModel.onAction(GamePlayAction.OnLetterClick(letter))
            }
        }

        viewModel.onAction(GamePlayAction.OnNicknameChange("ACE"))
        viewModel.onAction(GamePlayAction.OnNicknameConfirmed)

        assertThat(fakeRepository.savedResults.isNotEmpty()).isTrue()
        assertThat(fakeRepository.savedResults.last().won).isFalse()
        assertThat(fakeRepository.savedResults.last().nickname).isEqualTo("ACE")
        assertThat(viewModel.state.value.showNicknameDialog).isFalse()
    }

    @Test
    fun `guessing all letters shows nickname dialog with positive score`() = runTest {
        val word = viewModel.state.value.word
        word.toSet().forEach { letter ->
            if (viewModel.state.value.gameStatus == GameStatus.Playing) {
                viewModel.onAction(GamePlayAction.OnLetterClick(letter))
            }
        }

        val state = viewModel.state.value
        assertThat(state.gameStatus).isEqualTo(GameStatus.Won)
        assertThat(state.showNicknameDialog).isTrue()
        assertThat(state.pendingScore > 0).isTrue()
    }

    @Test
    fun `confirming nickname saves Won result`() = runTest {
        val word = viewModel.state.value.word
        word.toSet().forEach { letter ->
            if (viewModel.state.value.gameStatus == GameStatus.Playing) {
                viewModel.onAction(GamePlayAction.OnLetterClick(letter))
            }
        }

        viewModel.onAction(GamePlayAction.OnNicknameChange("HERO"))
        viewModel.onAction(GamePlayAction.OnNicknameConfirmed)

        assertThat(fakeRepository.savedResults.isNotEmpty()).isTrue()
        assertThat(fakeRepository.savedResults.last().won).isTrue()
        assertThat(fakeRepository.savedResults.last().nickname).isEqualTo("HERO")
    }

    @Test
    fun `blank nickname saves as ANON`() = runTest {
        val word = viewModel.state.value.word
        word.toSet().forEach { letter ->
            if (viewModel.state.value.gameStatus == GameStatus.Playing) {
                viewModel.onAction(GamePlayAction.OnLetterClick(letter))
            }
        }

        viewModel.onAction(GamePlayAction.OnNicknameConfirmed)

        assertThat(fakeRepository.savedResults.last().nickname).isEqualTo("ANON")
    }

    @Test
    fun `OnPlayAgain resets state with a new game`() = runTest {
        viewModel.onAction(GamePlayAction.OnLetterClick('A'))
        viewModel.onAction(GamePlayAction.OnPlayAgain)

        val state = viewModel.state.value
        assertThat(state.guessedLetters.isEmpty()).isTrue()
        assertThat(state.wrongGuesses).isEqualTo(0)
        assertThat(state.gameStatus).isEqualTo(GameStatus.Playing)
        assertThat(state.showNicknameDialog).isFalse()
    }

    @Test
    fun `OnViewLeaderboard sends NavigateToLeaderboard event`() = runTest {
        viewModel.events.test {
            viewModel.onAction(GamePlayAction.OnViewLeaderboard)
            assertThat(awaitItem()).isEqualTo(GamePlayEvent.NavigateToLeaderboard)
        }
    }

    @Test
    fun `OnNavigateHome sends NavigateHome event`() = runTest {
        viewModel.events.test {
            viewModel.onAction(GamePlayAction.OnNavigateHome)
            assertThat(awaitItem()).isEqualTo(GamePlayEvent.NavigateHome)
        }
    }

    @Test
    fun `nickname is capped at MAX_NICKNAME_LENGTH characters`() = runTest {
        viewModel.onAction(GamePlayAction.OnNicknameChange("ABCDEFGHIJKLMNOP"))
        assertThat(viewModel.state.value.nickname.length).isEqualTo(10)
    }
}
