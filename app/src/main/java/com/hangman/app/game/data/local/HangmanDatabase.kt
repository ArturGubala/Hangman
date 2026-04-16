package com.hangman.app.game.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [GameResultEntity::class],
    version = 2
)
abstract class HangmanDatabase : RoomDatabase() {
    abstract fun gameResultDao(): GameResultDao
}
