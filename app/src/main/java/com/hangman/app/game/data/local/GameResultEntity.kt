package com.hangman.app.game.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.hangman.app.game.domain.model.GameResult

@Entity(tableName = "game_results")
data class GameResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val word: String,
    val category: String,
    val attemptsUsed: Int,
    val maxAttempts: Int,
    val won: Boolean,
    val playedAt: Long
)

fun GameResultEntity.toGameResult(): GameResult = GameResult(
    id = id,
    word = word,
    category = category,
    attemptsUsed = attemptsUsed,
    maxAttempts = maxAttempts,
    won = won,
    playedAt = playedAt
)

fun GameResult.toGameResultEntity(): GameResultEntity = GameResultEntity(
    id = id,
    word = word,
    category = category,
    attemptsUsed = attemptsUsed,
    maxAttempts = maxAttempts,
    won = won,
    playedAt = playedAt
)
