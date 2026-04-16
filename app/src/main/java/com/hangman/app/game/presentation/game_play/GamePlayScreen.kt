package com.hangman.app.game.presentation.game_play

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hangman.app.R
import com.hangman.app.core.presentation.ObserveAsEvents
import com.hangman.app.core.presentation.designsystem.components.HangmanButton
import com.hangman.app.core.presentation.designsystem.components.HangmanOutlinedButton
import com.hangman.app.core.presentation.designsystem.components.HangmanScaffold
import com.hangman.app.core.presentation.designsystem.theme.HangmanTheme
import com.hangman.app.game.presentation.game_play.components.HangmanDrawing
import com.hangman.app.game.presentation.game_play.components.LetterKeyboard
import com.hangman.app.game.presentation.game_play.components.WordDisplay
import org.koin.androidx.compose.koinViewModel

@Composable
fun GamePlayRoot(
    onNavigateToLeaderboard: () -> Unit,
    onNavigateHome: () -> Unit,
    viewModel: GamePlayViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            GamePlayEvent.NavigateToLeaderboard -> onNavigateToLeaderboard()
            GamePlayEvent.NavigateHome -> onNavigateHome()
        }
    }

    GamePlayScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun GamePlayScreen(
    state: GamePlayState,
    onAction: (GamePlayAction) -> Unit,
    modifier: Modifier = Modifier
) {
    HangmanScaffold(
        title = stringResource(R.string.app_name),
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.game_category_label, state.category),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(
                        R.string.game_attempts_remaining,
                        (state.maxAttempts - state.wrongGuesses).coerceAtLeast(0)
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = when {
                        state.wrongGuesses >= state.maxAttempts - 1 -> MaterialTheme.colorScheme.error
                        else -> MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            HangmanDrawing(
                wrongGuesses = state.wrongGuesses,
                modifier = Modifier
                    .fillMaxWidth(0.55f)
                    .padding(vertical = 8.dp)
            )

            WordDisplay(
                displayWord = state.displayWord,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            when (state.gameStatus) {
                GameStatus.Playing -> {
                    LetterKeyboard(
                        guessedLetters = state.guessedLetters,
                        word = state.word,
                        enabled = true,
                        onLetterClick = { onAction(GamePlayAction.OnLetterClick(it)) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                GameStatus.Won -> {
                    GameResultBanner(
                        won = true,
                        revealedWord = null,
                        score = state.pendingScore,
                        onPlayAgain = { onAction(GamePlayAction.OnPlayAgain) },
                        onViewLeaderboard = { onAction(GamePlayAction.OnViewLeaderboard) },
                        onHome = { onAction(GamePlayAction.OnNavigateHome) }
                    )
                }
                GameStatus.Lost -> {
                    GameResultBanner(
                        won = false,
                        revealedWord = state.word,
                        score = state.pendingScore,
                        onPlayAgain = { onAction(GamePlayAction.OnPlayAgain) },
                        onViewLeaderboard = { onAction(GamePlayAction.OnViewLeaderboard) },
                        onHome = { onAction(GamePlayAction.OnNavigateHome) }
                    )
                }
            }
        }
    }

    if (state.showNicknameDialog) {
        NicknameDialog(
            score = state.pendingScore,
            won = state.gameStatus == GameStatus.Won,
            nickname = state.nickname,
            onNicknameChange = { onAction(GamePlayAction.OnNicknameChange(it)) },
            onConfirm = { onAction(GamePlayAction.OnNicknameConfirmed) },
            onSkip = { onAction(GamePlayAction.OnNicknameSkipped) }
        )
    }
}

@Composable
private fun NicknameDialog(
    score: Int,
    won: Boolean,
    nickname: String,
    onNicknameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onSkip: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onSkip,
        title = {
            Text(
                text = stringResource(R.string.nickname_dialog_title),
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = FontFamily.Monospace)
            )
        },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (won) {
                    Text(
                        text = stringResource(R.string.nickname_dialog_score, score),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = if (won) stringResource(R.string.nickname_dialog_subtitle_won)
                    else stringResource(R.string.nickname_dialog_subtitle_lost),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = nickname,
                    onValueChange = { onNicknameChange(it.filter { c -> c.isLetter() || c.isDigit() }) },
                    label = { Text(stringResource(R.string.nickname_dialog_field_label)) },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.Characters,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { onConfirm() }),
                    supportingText = {
                        Text(
                            text = "${nickname.length}/$MAX_NICKNAME_LENGTH",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End
                        )
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.nickname_dialog_confirm),
                    style = MaterialTheme.typography.labelLarge.copy(fontFamily = FontFamily.Monospace)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onSkip) {
                Text(
                    text = stringResource(R.string.nickname_dialog_skip),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    )
}

@Composable
private fun GameResultBanner(
    won: Boolean,
    revealedWord: String?,
    score: Int,
    onPlayAgain: () -> Unit,
    onViewLeaderboard: () -> Unit,
    onHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (won) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (won) stringResource(R.string.game_won_title)
                else stringResource(R.string.game_lost_title),
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            if (won && score > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(R.string.game_score_display, score),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }
            if (!won && revealedWord != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.game_lost_word_reveal, revealedWord),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            HangmanButton(
                text = stringResource(R.string.game_play_again),
                onClick = onPlayAgain
            )
            Spacer(modifier = Modifier.height(8.dp))
            HangmanOutlinedButton(
                text = stringResource(R.string.game_view_leaderboard),
                onClick = onViewLeaderboard
            )
            Spacer(modifier = Modifier.height(8.dp))
            HangmanOutlinedButton(
                text = stringResource(R.string.game_home),
                onClick = onHome
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun GamePlayScreenPlayingPreview() {
    HangmanTheme {
        GamePlayScreen(
            state = GamePlayState(
                word = "ELEPHANT",
                category = "Animals",
                displayWord = "E L _ _ H _ N T",
                guessedLetters = setOf('E', 'L', 'H', 'N', 'T', 'X'),
                wrongGuesses = 1,
                maxAttempts = 6,
                gameStatus = GameStatus.Playing
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GamePlayScreenWonPreview() {
    HangmanTheme {
        GamePlayScreen(
            state = GamePlayState(
                word = "ELEPHANT",
                category = "Animals",
                displayWord = "E L E P H A N T",
                gameStatus = GameStatus.Won,
                wrongGuesses = 2,
                maxAttempts = 6,
                pendingScore = 1000
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GamePlayScreenLostPreview() {
    HangmanTheme {
        GamePlayScreen(
            state = GamePlayState(
                word = "ELEPHANT",
                category = "Animals",
                displayWord = "_ _ _ _ _ _ _ _",
                gameStatus = GameStatus.Lost,
                wrongGuesses = 6,
                maxAttempts = 6,
                pendingScore = 0
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun NicknameDialogPreview() {
    HangmanTheme {
        GamePlayScreen(
            state = GamePlayState(
                word = "ELEPHANT",
                category = "Animals",
                displayWord = "E L E P H A N T",
                gameStatus = GameStatus.Won,
                wrongGuesses = 1,
                maxAttempts = 6,
                pendingScore = 1200,
                showNicknameDialog = true,
                nickname = "ACE"
            ),
            onAction = {}
        )
    }
}
