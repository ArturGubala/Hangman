package com.hangman.app.game.presentation.game_play

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hangman.app.game.domain.GameEngine
import com.hangman.app.game.domain.data.LeaderboardRepository
import com.hangman.app.game.domain.model.GameResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GamePlayViewModel(
    private val gameEngine: GameEngine,
    private val leaderboardRepository: LeaderboardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GamePlayState())
    val state = _state.asStateFlow()

    private val _events = Channel<GamePlayEvent>()
    val events = _events.receiveAsFlow()

    init {
        startNewGame()
    }

    fun onAction(action: GamePlayAction) {
        when (action) {
            is GamePlayAction.OnLetterClick -> guessLetter(action.letter)
            GamePlayAction.OnPlayAgain -> startNewGame()
            GamePlayAction.OnViewLeaderboard -> {
                viewModelScope.launch {
                    _events.send(GamePlayEvent.NavigateToLeaderboard)
                }
            }
            GamePlayAction.OnNavigateHome -> {
                viewModelScope.launch {
                    _events.send(GamePlayEvent.NavigateHome)
                }
            }
        }
    }

    private fun startNewGame() {
        val (word, category) = gameEngine.getRandomWord()
        _state.update {
            GamePlayState(
                word = word,
                category = category,
                displayWord = gameEngine.displayWord(word, emptySet()),
                guessedLetters = emptySet(),
                wrongGuesses = 0,
                maxAttempts = gameEngine.maxAttempts,
                gameStatus = GameStatus.Playing,
                isLoading = false
            )
        }
    }

    private fun guessLetter(letter: Char) {
        val current = _state.value
        if (current.gameStatus != GameStatus.Playing) return
        if (letter in current.guessedLetters) return

        val updatedGuessed = current.guessedLetters + letter
        val wrongGuesses = gameEngine.wrongGuessCount(current.word, updatedGuessed)
        val won = gameEngine.isWon(current.word, updatedGuessed)
        val lost = wrongGuesses >= current.maxAttempts

        val newStatus = when {
            won -> GameStatus.Won
            lost -> GameStatus.Lost
            else -> GameStatus.Playing
        }

        _state.update {
            it.copy(
                guessedLetters = updatedGuessed,
                displayWord = gameEngine.displayWord(current.word, updatedGuessed),
                wrongGuesses = wrongGuesses,
                gameStatus = newStatus
            )
        }

        if (newStatus != GameStatus.Playing) {
            saveResult(newStatus == GameStatus.Won)
        }
    }

    private fun saveResult(won: Boolean) {
        val current = _state.value
        viewModelScope.launch {
            leaderboardRepository.saveResult(
                GameResult(
                    word = current.word,
                    category = current.category,
                    attemptsUsed = current.wrongGuesses,
                    maxAttempts = current.maxAttempts,
                    won = won,
                    playedAt = System.currentTimeMillis()
                )
            )
        }
    }
}
