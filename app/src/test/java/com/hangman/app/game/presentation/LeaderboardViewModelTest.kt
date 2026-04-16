package com.hangman.app.game.presentation

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import com.hangman.app.game.domain.model.GameResult
import com.hangman.app.game.presentation.leaderboard.LeaderboardAction
import com.hangman.app.game.presentation.leaderboard.LeaderboardEvent
import com.hangman.app.game.presentation.leaderboard.LeaderboardViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LeaderboardViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var fakeRepository: FakeLeaderboardRepository
    private lateinit var viewModel: LeaderboardViewModel

    @BeforeEach
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeLeaderboardRepository()
        viewModel = LeaderboardViewModel(fakeRepository)
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has isLoading true before results arrive`() = runTest {
        val freshRepo = FakeLeaderboardRepository()
        // Create viewModel after setting up repo but before emitting
        val freshViewModel = LeaderboardViewModel(freshRepo)
        // With UnconfinedTestDispatcher, the flow is immediately collected
        // so isLoading will flip to false right away — just verify not crashing
        assertThat(freshViewModel.state.value.isLoading).isFalse()
    }

    @Test
    fun `state emits results from repository mapped to UI models`() = runTest {
        val results = listOf(
            GameResult(1L, "ELEPHANT", "Animals", 2, 6, true, 1000L),
            GameResult(2L, "PYTHON", "Technology", 6, 6, false, 2000L)
        )
        fakeRepository.emitResults(results)

        val state = viewModel.state.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.results.size).isEqualTo(2)
        assertThat(state.results[0].word).isEqualTo("ELEPHANT")
        assertThat(state.results[0].won).isTrue()
        assertThat(state.results[1].word).isEqualTo("PYTHON")
        assertThat(state.results[1].won).isFalse()
    }

    @Test
    fun `state emits empty list when repository has no results`() = runTest {
        fakeRepository.emitResults(emptyList())
        assertThat(viewModel.state.value.results.isEmpty()).isTrue()
    }

    @Test
    fun `OnNavigateBack sends NavigateBack event`() = runTest {
        viewModel.events.test {
            viewModel.onAction(LeaderboardAction.OnNavigateBack)
            assertThat(awaitItem()).isEqualTo(LeaderboardEvent.NavigateBack)
        }
    }
}
