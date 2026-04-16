package com.hangman.app.game.data.di

import androidx.room.Room
import com.hangman.app.game.data.local.HangmanDatabase
import com.hangman.app.game.data.repository.RoomLeaderboardRepository
import com.hangman.app.game.domain.data.LeaderboardRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val gameDataModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            HangmanDatabase::class.java,
            "hangman.db"
        ).fallbackToDestructiveMigration().build()
    }
    single { get<HangmanDatabase>().gameResultDao() }
    single<LeaderboardRepository> { RoomLeaderboardRepository(get()) }
}
