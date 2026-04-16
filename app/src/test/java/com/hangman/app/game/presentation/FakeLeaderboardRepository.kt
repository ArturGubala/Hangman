package com.hangman.app.game.presentation

import com.hangman.app.core.domain.util.DataError
import com.hangman.app.core.domain.util.EmptyResult
import com.hangman.app.core.domain.util.Result
import com.hangman.app.game.domain.data.LeaderboardRepository
import com.hangman.app.game.domain.model.GameResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeLeaderboardRepository : LeaderboardRepository {

    private val _results = MutableStateFlow<List<GameResult>>(emptyList())
    var shouldReturnError = false

    val savedResults: List<GameResult>
        get() = _results.value

    fun emitResults(results: List<GameResult>) {
        _results.update { results }
    }

    override fun getResults(): Flow<List<GameResult>> = _results

    override suspend fun saveResult(result: GameResult): EmptyResult<DataError.Local> {
        return if (shouldReturnError) {
            Result.Error(DataError.Local.UNKNOWN)
        } else {
            _results.update { it + result }
            Result.Success(Unit)
        }
    }
}
