package com.hangman.app.game.presentation.leaderboard

import com.hangman.app.game.domain.model.GameResult

data class GameResultUi(
    val id: Long,
    val nickname: String,
    val score: Int,
    val word: String,
    val category: String,
    val won: Boolean
)

fun GameResult.toGameResultUi(): GameResultUi = GameResultUi(
    id = id,
    nickname = nickname,
    score = score,
    word = word,
    category = category,
    won = won
)
