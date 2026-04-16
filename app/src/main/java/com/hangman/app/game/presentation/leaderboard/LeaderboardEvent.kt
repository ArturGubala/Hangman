package com.hangman.app.game.presentation.leaderboard

sealed interface LeaderboardEvent {
    data object NavigateBack : LeaderboardEvent
}
