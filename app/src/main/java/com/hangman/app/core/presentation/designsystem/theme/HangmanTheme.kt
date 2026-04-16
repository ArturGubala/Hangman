package com.hangman.app.core.presentation.designsystem.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = HangmanGreen,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = HangmanGreenLight,
    onPrimaryContainer = HangmanGreenDark,
    error = HangmanRed,
    onError = androidx.compose.ui.graphics.Color.White,
    errorContainer = HangmanRedLight,
    onErrorContainer = HangmanRedDark,
    background = HangmanBackground,
    onBackground = HangmanOnSurface,
    surface = HangmanSurface,
    onSurface = HangmanOnSurface
)

@Composable
fun HangmanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = HangmanTypography,
        content = content
    )
}
