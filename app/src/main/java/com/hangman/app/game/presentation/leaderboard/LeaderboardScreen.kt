package com.hangman.app.game.presentation.leaderboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.hangman.app.R
import com.hangman.app.core.presentation.ObserveAsEvents
import com.hangman.app.core.presentation.designsystem.components.HangmanScaffold
import com.hangman.app.core.presentation.designsystem.theme.HangmanTheme
import org.koin.androidx.compose.koinViewModel

const val TAG_EMPTY_STATE = "empty_state"

@Composable
fun LeaderboardRoot(
    onNavigateBack: () -> Unit,
    viewModel: LeaderboardViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            LeaderboardEvent.NavigateBack -> onNavigateBack()
        }
    }

    LeaderboardScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun LeaderboardScreen(
    state: LeaderboardState,
    onAction: (LeaderboardAction) -> Unit,
    modifier: Modifier = Modifier
) {
    HangmanScaffold(
        title = stringResource(R.string.leaderboard_title),
        showBackButton = true,
        onBackClick = { onAction(LeaderboardAction.OnNavigateBack) },
        modifier = modifier
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                state.results.isEmpty() -> {
                    Text(
                        text = stringResource(R.string.leaderboard_empty),
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(32.dp)
                            .testTag(TAG_EMPTY_STATE)
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                    ) {
                        items(
                            items = state.results,
                            key = { it.id }
                        ) { result ->
                            GameResultCard(result = result)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun GameResultCard(
    result: GameResultUi,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (result.won) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = result.word,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = result.category,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
                Surface(
                    color = if (result.won) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error,
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = if (result.won) stringResource(R.string.leaderboard_won)
                        else stringResource(R.string.leaderboard_lost),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(R.string.leaderboard_attempts, result.attemptsUsed, result.maxAttempts),
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = result.formattedDate,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LeaderboardScreenWithResultsPreview() {
    HangmanTheme {
        LeaderboardScreen(
            state = LeaderboardState(
                results = listOf(
                    GameResultUi(1L, "ELEPHANT", "Animals", 2, 6, true, "Apr 16, 2026 · 10:30"),
                    GameResultUi(2L, "PYTHON", "Technology", 6, 6, false, "Apr 15, 2026 · 18:00")
                )
            ),
            onAction = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun LeaderboardScreenEmptyPreview() {
    HangmanTheme {
        LeaderboardScreen(
            state = LeaderboardState(results = emptyList()),
            onAction = {}
        )
    }
}
