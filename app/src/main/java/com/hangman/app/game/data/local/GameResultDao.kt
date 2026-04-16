package com.hangman.app.game.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface GameResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: GameResultEntity)

    @Query("SELECT * FROM game_results ORDER BY score DESC, playedAt DESC")
    fun getAllResults(): Flow<List<GameResultEntity>>
}
