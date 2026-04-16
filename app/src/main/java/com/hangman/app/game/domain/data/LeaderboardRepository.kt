package com.hangman.app.game.domain.data

import com.hangman.app.core.domain.util.DataError
import com.hangman.app.core.domain.util.EmptyResult
import com.hangman.app.game.domain.model.GameResult
import kotlinx.coroutines.flow.Flow

interface LeaderboardRepository {
    fun getResults(): Flow<List<GameResult>>
    suspend fun saveResult(result: GameResult): EmptyResult<DataError.Local>
}
