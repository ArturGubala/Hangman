package com.hangman.app.game.presentation.home

sealed interface HomeEvent {
    data object NavigateToGame : HomeEvent
    data object NavigateToLeaderboard : HomeEvent
}
