package com.hangman.app.game.presentation.game_play

import androidx.compose.runtime.Stable

enum class GameStatus { Playing, Won, Lost }

const val MAX_NICKNAME_LENGTH = 10

@Stable
data class GamePlayState(
    val word: String = "",
    val category: String = "",
    val displayWord: String = "",
    val guessedLetters: Set<Char> = emptySet(),
    val wrongGuesses: Int = 0,
    val maxAttempts: Int = 6,
    val gameStatus: GameStatus = GameStatus.Playing,
    val isLoading: Boolean = false,
    // Nickname dialog — shown once when game ends
    val showNicknameDialog: Boolean = false,
    val nickname: String = "",
    val pendingScore: Int = 0
)
