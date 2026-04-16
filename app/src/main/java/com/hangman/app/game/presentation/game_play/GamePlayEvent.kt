package com.hangman.app.game.presentation.game_play

sealed interface GamePlayEvent {
    data object NavigateToLeaderboard : GamePlayEvent
    data object NavigateHome : GamePlayEvent
}
