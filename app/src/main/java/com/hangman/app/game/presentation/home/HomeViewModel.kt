package com.hangman.app.game.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state = _state.asStateFlow()

    private val _events = Channel<HomeEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnStartGame -> {
                viewModelScope.launch {
                    _events.send(HomeEvent.NavigateToGame)
                }
            }
            HomeAction.OnViewLeaderboard -> {
                viewModelScope.launch {
                    _events.send(HomeEvent.NavigateToLeaderboard)
                }
            }
        }
    }
}
