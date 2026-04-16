package com.hangman.app.game.presentation.game_play.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hangman.app.R
import com.hangman.app.core.presentation.designsystem.theme.HangmanTheme

private val ALPHABET = ('A'..'Z').toList()

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LetterKeyboard(
    guessedLetters: Set<Char>,
    word: String,
    enabled: Boolean,
    onLetterClick: (Char) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ALPHABET.forEach { letter ->
            val isGuessed = letter in guessedLetters
            val isCorrect = isGuessed && letter in word
            val contentDesc = stringResource(R.string.cd_letter_button, letter)

            OutlinedButton(
                onClick = { onLetterClick(letter) },
                enabled = enabled && !isGuessed,
                modifier = Modifier
                    .size(40.dp)
                    .padding(2.dp)
                    .semantics { contentDescription = contentDesc },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = when {
                        isCorrect -> MaterialTheme.colorScheme.primaryContainer
                        isGuessed -> MaterialTheme.colorScheme.errorContainer
                        else -> MaterialTheme.colorScheme.surface
                    }
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
            ) {
                Text(
                    text = letter.toString(),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LetterKeyboardPreview() {
    HangmanTheme {
        LetterKeyboard(
            guessedLetters = setOf('A', 'E', 'X', 'Z'),
            word = "ELEPHANT",
            enabled = true,
            onLetterClick = {}
        )
    }
}
