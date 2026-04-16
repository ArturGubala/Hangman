package com.hangman.app.game.presentation.leaderboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hangman.app.game.domain.data.LeaderboardRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LeaderboardViewModel(
    private val leaderboardRepository: LeaderboardRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LeaderboardState(isLoading = true))
    val state = _state.asStateFlow()

    private val _events = Channel<LeaderboardEvent>()
    val events = _events.receiveAsFlow()

    init {
        leaderboardRepository.getResults()
            .onEach { results ->
                _state.update {
                    it.copy(
                        results = results.map { r -> r.toGameResultUi() },
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    fun onAction(action: LeaderboardAction) {
        when (action) {
            LeaderboardAction.OnNavigateBack -> {
                viewModelScope.launch {
                    _events.send(LeaderboardEvent.NavigateBack)
                }
            }
        }
    }
}
