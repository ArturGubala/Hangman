package com.hangman.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.hangman.app.core.presentation.designsystem.theme.HangmanTheme
import com.hangman.app.game.presentation.navigation.HangmanNavHost

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HangmanTheme {
                val navController = rememberNavController()
                HangmanNavHost(navController = navController)
            }
        }
    }
}
