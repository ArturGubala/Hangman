package com.hangman.app.game.presentation.game_play

sealed interface GamePlayAction {
    data class OnLetterClick(val letter: Char) : GamePlayAction
    data object OnPlayAgain : GamePlayAction
    data object OnViewLeaderboard : GamePlayAction
    data object OnNavigateHome : GamePlayAction
}
