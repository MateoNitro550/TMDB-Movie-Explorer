package com.example.tmdb.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tmdb.data.MovieDetails
import com.example.tmdb.data.MovieSummary

private const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: MovieUiState,
    onMovieClick: (Int) -> Unit,
    onWatchlistClick: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Top 20 Trending Movies Today", fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(onClick = onWatchlistClick) {
                        Text("Watchlist")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            state.errorMessage?.let { message ->
                ErrorMessage(message = message, onRetry = onRetry)
            }

            if (state.isCatalogLoading) {
                LoadingMessage(text = "Loading popular movies...")
            } else {
                MovieList(
                    movies = state.movies,
                    onMovieClick = onMovieClick
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    state: MovieUiState,
    onBackClick: () -> Unit,
    onAddToWatchlist: () -> Unit,
    onRetry: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Details") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when {
                state.isDetailsLoading -> LoadingMessage(text = "Loading details...")
                state.selectedMovie != null -> MovieDetailsCard(
                    movie = state.selectedMovie,
                    onAddToWatchlist = onAddToWatchlist
                )
                state.errorMessage != null -> ErrorMessage(
                    message = state.errorMessage,
                    onRetry = onRetry
                )
                else -> Text("Choose a movie from the home screen.")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchlistScreen(
    state: MovieUiState,
    onBackClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onRemoveClick: (Int) -> Unit
) {
    val watchlistCount by remember(state.watchlist) {
        derivedStateOf { state.watchlist.size }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Watchlist") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Saved $watchlistCount Movies",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            if (state.watchlist.isEmpty()) {
                Text("Your watchlist is empty. Open a movie detail page and save a movie.")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(state.watchlist, key = { it.id }) { movie ->
                        MovieRow(
                            movie = movie,
                            onMovieClick = onMovieClick,
                            trailingContent = {
                                OutlinedButton(onClick = { onRemoveClick(movie.id) }) {
                                    Text("Remove")
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieList(
    movies: List<MovieSummary>,
    onMovieClick: (Int) -> Unit
) {
    if (movies.isEmpty()) {
        Text("No movies are available right now.")
        return
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        items(movies, key = { it.id }) { movie ->
            MovieRow(movie = movie, onMovieClick = onMovieClick)
        }
    }
}

@Composable
private fun MovieRow(
    movie: MovieSummary,
    onMovieClick: (Int) -> Unit,
    trailingContent: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onMovieClick(movie.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(movie.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text("Released: ${movie.releaseDate.ifBlank { "Unknown" }}")
                Text("Rating: ${movie.voteAverage}")
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = movie.overview.ifBlank { "No overview available." },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            trailingContent?.invoke()
        }
    }
}

@Composable
private fun MovieDetailsCard(
    movie: MovieDetails,
    onAddToWatchlist: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        MoviePoster(posterPath = movie.posterPath, title = movie.title)
        Text(movie.title, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Released: ${movie.releaseDate.ifBlank { "Unknown" }}")
        Text("Runtime: ${movie.runtime?.let { "$it minutes" } ?: "Unknown"}")
        Text("Rating: ${movie.voteAverage}")
        Text("Genres: ${movie.genres.joinToString { it.name }.ifBlank { "Unknown" }}")
        Text(movie.overview.ifBlank { "No overview available." })
        Button(onClick = onAddToWatchlist) {
            Text("Add to Watchlist")
        }
    }
}

@Composable
private fun MoviePoster(posterPath: String?, title: String) {
    if (posterPath.isNullOrBlank()) {
        return
    }

    AsyncImage(
        model = "$POSTER_BASE_URL$posterPath",
        contentDescription = "$title poster",
        modifier = Modifier
            .fillMaxWidth()
            .height(420.dp),
        contentScale = ContentScale.Fit
    )
}

@Composable
private fun ErrorMessage(message: String, onRetry: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(message, color = MaterialTheme.colorScheme.onErrorContainer)
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}

@Composable
private fun LoadingMessage(text: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CircularProgressIndicator()
        Text(text)
    }
}
