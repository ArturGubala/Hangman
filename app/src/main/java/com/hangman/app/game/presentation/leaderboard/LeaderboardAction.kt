package com.hangman.app.game.presentation.leaderboard

sealed interface LeaderboardAction {
    data object OnNavigateBack : LeaderboardAction
}
