package com.hangman.app

import android.app.Application
import com.hangman.app.game.data.di.gameDataModule
import com.hangman.app.game.presentation.di.gamePresentationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class HangmanApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@HangmanApp)
            modules(
                gameDataModule,
                gamePresentationModule
            )
        }
    }
}
