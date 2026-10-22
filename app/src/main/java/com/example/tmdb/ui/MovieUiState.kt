package com.example.tmdb.ui

import com.example.tmdb.data.MovieDetails
import com.example.tmdb.data.MovieSummary

data class MovieUiState(
    val isCatalogLoading: Boolean = false,
    val isDetailsLoading: Boolean = false,
    val movies: List<MovieSummary> = emptyList(),
    val selectedMovie: MovieDetails? = null,
    val watchlist: List<MovieSummary> = emptyList(),
    val errorMessage: String? = null
)
