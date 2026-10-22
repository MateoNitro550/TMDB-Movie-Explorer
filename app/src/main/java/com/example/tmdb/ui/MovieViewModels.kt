package com.example.tmdb.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tmdb.data.MovieDetails
import com.example.tmdb.data.MovieRepository
import com.example.tmdb.data.MovieSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MovieViewModel(
    private val repository: MovieRepository = MovieRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(MovieUiState(isCatalogLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        loadPopularMovies()
    }

    fun loadPopularMovies() {
        _uiState.update { it.copy(isCatalogLoading = true, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.fetchPopularMovies()
            result.onSuccess { movies ->
                _uiState.update { current ->
                    current.copy(
                        isCatalogLoading = false,
                        movies = movies,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isCatalogLoading = false,
                        errorMessage = error.localizedMessage ?: "Unable to load movies."
                    )
                }
            }
        }
    }

    fun loadMovieDetails(movieId: Int) {
        _uiState.update { it.copy(isDetailsLoading = true, selectedMovie = null, errorMessage = null) }

        viewModelScope.launch {
            val result = repository.fetchMovieDetails(movieId)
            result.onSuccess { movie ->
                _uiState.update {
                    it.copy(
                        isDetailsLoading = false,
                        selectedMovie = movie,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isDetailsLoading = false,
                        errorMessage = error.localizedMessage ?: "Unable to load movie details."
                    )
                }
            }
        }
    }

    fun addSelectedMovieToWatchlist() {
        val selectedMovie = _uiState.value.selectedMovie ?: return
        addToWatchlist(selectedMovie.toSummary())
    }

    fun addToWatchlist(movie: MovieSummary) {
        _uiState.update { current ->
            if (current.watchlist.any { it.id == movie.id }) {
                current
            } else {
                current.copy(watchlist = current.watchlist + movie)
            }
        }
    }

    fun removeFromWatchlist(movieId: Int) {
        _uiState.update { current ->
            current.copy(watchlist = current.watchlist.filterNot { it.id == movieId })
        }
    }

    private fun MovieDetails.toSummary(): MovieSummary {
        return MovieSummary(
            id = id,
            title = title,
            overview = overview,
            posterPath = posterPath,
            releaseDate = releaseDate,
            voteAverage = voteAverage
        )
    }
}
