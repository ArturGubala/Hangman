package com.hangman.app.game.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.hangman.app.game.presentation.game_play.GamePlayRoot
import com.hangman.app.game.presentation.home.HomeRoot
import com.hangman.app.game.presentation.leaderboard.LeaderboardRoot

@Composable
fun HangmanNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier
    ) {
        composable<HomeRoute> {
            HomeRoot(
                onNavigateToGame = { navController.navigate(GamePlayRoute) },
                onNavigateToLeaderboard = { navController.navigate(LeaderboardRoute) }
            )
        }
        composable<GamePlayRoute> {
            GamePlayRoot(
                onNavigateToLeaderboard = {
                    navController.navigate(LeaderboardRoute) {
                        popUpTo(GamePlayRoute) { inclusive = true }
                    }
                },
                onNavigateHome = {
                    navController.navigate(HomeRoute) {
                        popUpTo(HomeRoute) { inclusive = true }
                    }
                }
            )
        }
        composable<LeaderboardRoute> {
            LeaderboardRoot(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
