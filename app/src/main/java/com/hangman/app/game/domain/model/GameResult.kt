package com.hangman.app.game.domain.model

data class GameResult(
    val id: Long = 0,
    val word: String,
    val category: String,
    val attemptsUsed: Int,
    val maxAttempts: Int,
    val won: Boolean,
    val playedAt: Long
)
