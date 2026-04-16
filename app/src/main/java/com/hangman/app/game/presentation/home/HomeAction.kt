package com.hangman.app.game.presentation.home

sealed interface HomeAction {
    data object OnStartGame : HomeAction
    data object OnViewLeaderboard : HomeAction
}
