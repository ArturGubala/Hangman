package com.hangman.app.game.presentation.leaderboard

import com.hangman.app.game.domain.model.GameResult
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GameResultUi(
    val id: Long,
    val word: String,
    val category: String,
    val attemptsUsed: Int,
    val maxAttempts: Int,
    val won: Boolean,
    val formattedDate: String
)

fun GameResult.toGameResultUi(): GameResultUi {
    val dateFormat = SimpleDateFormat("MMM d, yyyy · HH:mm", Locale.getDefault())
    return GameResultUi(
        id = id,
        word = word,
        category = category,
        attemptsUsed = attemptsUsed,
        maxAttempts = maxAttempts,
        won = won,
        formattedDate = dateFormat.format(Date(playedAt))
    )
}
