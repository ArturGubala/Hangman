package com.hangman.app.game.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeRoot(
    onNavigateToGame: () -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    viewModel: HomeViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            HomeEvent.NavigateToGame -> onNavigateToGame()
            HomeEvent.NavigateToLeaderboard -> onNavigateToLeaderboard()
        }
    }

    HomeScreen(
        state = state,
        onAction = viewModel::onAction
    )
}

@Composable
fun HomeScreen(
    state: HomeState,
    onAction: (HomeAction) -> Unit,
    modifier: Modifier = Modifier
) {
    HangmanScaffold(
        title = stringResource(R.string.home_title),
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "🪢",
                style = MaterialTheme.typography.displayLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.home_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(48.dp))
            HangmanButton(
                text = stringResource(R.string.home_start_game),
                onClick = { onAction(HomeAction.OnStartGame) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            HangmanOutlinedButton(
                text = stringResource(R.string.home_leaderboard),
                onClick = { onAction(HomeAction.OnViewLeaderboard) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeScreenPreview() {
    HangmanTheme {
        HomeScreen(
            state = HomeState(),
            onAction = {}
        )
    }
}
