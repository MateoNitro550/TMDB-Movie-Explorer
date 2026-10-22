package com.example.tmdb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tmdb.ui.HomeScreen
import com.example.tmdb.ui.MovieDetailScreen
import com.example.tmdb.ui.MovieUiState
import com.example.tmdb.ui.MovieViewModel
import com.example.tmdb.ui.WatchlistScreen

object Routes {
    const val HOME = "home"
    const val MOVIE_DETAILS = "movie/{movieId}"
    const val WATCHLIST = "watchlist"

    fun movieDetails(movieId: Int): String = "movie/$movieId"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: MovieViewModel,
    state: MovieUiState
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                state = state,
                onMovieClick = { movieId -> navController.navigate(Routes.movieDetails(movieId)) },
                onWatchlistClick = { navController.navigate(Routes.WATCHLIST) },
                onRetry = viewModel::loadPopularMovies
            )
        }

        composable(Routes.MOVIE_DETAILS) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getString("movieId")?.toIntOrNull() ?: 0

            LaunchedEffect(movieId) {
                if (movieId > 0) {
                    viewModel.loadMovieDetails(movieId)
                }
            }

            MovieDetailScreen(
                state = state,
                onBackClick = { navController.popBackStack() },
                onAddToWatchlist = viewModel::addSelectedMovieToWatchlist,
                onRetry = { viewModel.loadMovieDetails(movieId) }
            )
        }

        composable(Routes.WATCHLIST) {
            WatchlistScreen(
                state = state,
                onBackClick = { navController.popBackStack() },
                onMovieClick = { movieId -> navController.navigate(Routes.movieDetails(movieId)) },
                onRemoveClick = viewModel::removeFromWatchlist
            )
        }
    }
}
