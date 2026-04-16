package com.hangman.app.game.presentation.leaderboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
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

private val MONO = FontFamily.Monospace

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ── Title ─────────────────────────────────────────────────
            Text(
                text = stringResource(R.string.leaderboard_high_scores),
                style = MaterialTheme.typography.titleLarge.copy(fontFamily = MONO),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (state.results.isEmpty()) {
                // ── Empty state ───────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag(TAG_EMPTY_STATE),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.leaderboard_empty_title),
                            style = MaterialTheme.typography.titleMedium.copy(fontFamily = MONO),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = stringResource(R.string.leaderboard_empty_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                // ── Table header ──────────────────────────────────────
                ScoreRow(
                    rank = "#",
                    name = "NAME",
                    score = "SCORE",
                    rankColor = MaterialTheme.colorScheme.primary,
                    nameColor = MaterialTheme.colorScheme.primary,
                    scoreColor = MaterialTheme.colorScheme.primary,
                    isHeader = true
                )
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.primary,
                    thickness = 1.dp
                )
                Spacer(modifier = Modifier.height(4.dp))

                // ── Score rows ────────────────────────────────────────
                LazyColumn(verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    itemsIndexed(
                        items = state.results,
                        key = { _, result -> result.id }
                    ) { index, result ->
                        val rank = index + 1
                        val rankColor = when (rank) {
                            1 -> Color(0xFFFFB300)   // gold
                            2 -> Color(0xFF9E9E9E)   // silver
                            3 -> Color(0xFF8D6E63)   // bronze
                            else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        }
                        val rowBg = if (index % 2 == 0)
                            MaterialTheme.colorScheme.surface
                        else
                            MaterialTheme.colorScheme.background

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(rowBg)
                        ) {
                            ScoreRow(
                                rank = rank.toString().padStart(2),
                                name = result.nickname.uppercase().take(10).padEnd(10),
                                score = result.score.toString().padStart(6),
                                rankColor = rankColor,
                                nameColor = if (result.won) MaterialTheme.colorScheme.onSurface
                                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                                scoreColor = if (result.won) MaterialTheme.colorScheme.primary
                                             else MaterialTheme.colorScheme.error,
                                isHeader = false
                            )
                        }

                        if (index < state.results.lastIndex) {
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant,
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScoreRow(
    rank: String,
    name: String,
    score: String,
    rankColor: Color,
    nameColor: Color,
    scoreColor: Color,
    isHeader: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = if (isHeader) 6.dp else 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rank,
            fontFamily = MONO,
            fontWeight = FontWeight.Bold,
            style = if (isHeader) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyLarge,
            color = rankColor,
            modifier = Modifier.width(32.dp),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = name,
            fontFamily = MONO,
            fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
            style = if (isHeader) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyLarge,
            color = nameColor,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = score,
            fontFamily = MONO,
            fontWeight = FontWeight.Bold,
            style = if (isHeader) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyLarge,
            color = scoreColor,
            textAlign = TextAlign.End,
            modifier = Modifier.width(72.dp)
        )
    }
}

// ── Previews ──────────────────────────────────────────────────────────────────

@Preview(showBackground = true)
@Composable
private fun LeaderboardScreenWithResultsPreview() {
    HangmanTheme {
        LeaderboardScreen(
            state = LeaderboardState(
                results = listOf(
                    GameResultUi(1L, "ARTURO", 1450, "SKATEBOARD", "Sports", true),
                    GameResultUi(2L, "PLAYER1", 1200, "ELEPHANT", "Animals", true),
                    GameResultUi(3L, "ACE", 1000, "SPAGHETTI", "Food", true),
                    GameResultUi(4L, "ANON", 800, "ALGORITHM", "Technology", true),
                    GameResultUi(5L, "XYZ", 0, "CROCODILE", "Animals", false)
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
