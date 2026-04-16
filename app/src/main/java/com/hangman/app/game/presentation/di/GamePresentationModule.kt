package com.hangman.app.game.presentation.di

import com.hangman.app.game.domain.GameEngine
import com.hangman.app.game.presentation.game_play.GamePlayViewModel
import com.hangman.app.game.presentation.home.HomeViewModel
import com.hangman.app.game.presentation.leaderboard.LeaderboardViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val gamePresentationModule = module {
    single { GameEngine() }
    viewModelOf(::HomeViewModel)
    viewModelOf(::GamePlayViewModel)
    viewModelOf(::LeaderboardViewModel)
}
