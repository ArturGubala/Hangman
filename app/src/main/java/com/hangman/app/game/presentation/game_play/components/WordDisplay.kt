package com.hangman.app.game.presentation.game_play.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hangman.app.core.presentation.designsystem.theme.HangmanTheme

@Composable
fun WordDisplay(
    displayWord: String,
    modifier: Modifier = Modifier
) {
    val letters = displayWord.split(" ")
    Row(
        modifier = modifier.padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.Bottom
    ) {
        letters.forEach { letter ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .widthIn(min = 24.dp)
                    .padding(horizontal = 2.dp)
            ) {
                Text(
                    text = if (letter == "_") " " else letter,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 26.sp
                    ),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "—",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WordDisplayPreview() {
    HangmanTheme {
        WordDisplay(displayWord = "H _ N G M _ N")
    }
}
