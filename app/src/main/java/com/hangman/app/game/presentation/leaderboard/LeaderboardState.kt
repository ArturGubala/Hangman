package com.hangman.app.game.presentation.leaderboard

import androidx.compose.runtime.Stable

@Stable
data class LeaderboardState(
    val results: List<GameResultUi> = emptyList(),
    val isLoading: Boolean = false
)
