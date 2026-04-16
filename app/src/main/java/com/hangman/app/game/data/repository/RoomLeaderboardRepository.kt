package com.hangman.app.game.data.repository

import com.hangman.app.core.domain.util.DataError
import com.hangman.app.core.domain.util.EmptyResult
import com.hangman.app.core.domain.util.Result
import com.hangman.app.game.data.local.GameResultDao
import com.hangman.app.game.data.local.toGameResult
import com.hangman.app.game.data.local.toGameResultEntity
import com.hangman.app.game.domain.data.LeaderboardRepository
import com.hangman.app.game.domain.model.GameResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomLeaderboardRepository(
    private val dao: GameResultDao
) : LeaderboardRepository {

    override fun getResults(): Flow<List<GameResult>> {
        return dao.getAllResults().map { entities ->
            entities.map { it.toGameResult() }
        }
    }

    override suspend fun saveResult(result: GameResult): EmptyResult<DataError.Local> {
        return try {
            dao.insert(result.toGameResultEntity())
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Local.UNKNOWN)
        }
    }
}
