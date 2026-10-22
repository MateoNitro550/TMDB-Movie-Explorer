package com.example.tmdb.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieCatalogResponse(
    val results: List<MovieSummary>,
)

@Serializable
data class MovieSummary(
    val id: Int,
    val title: String,
    val overview: String,
    @SerialName("poster_path") val posterPath: String,
    @SerialName("release_date") val releaseDate: String,
    @SerialName("vote_average") val voteAverage: Double
)

@Serializable
data class MovieDetails(
    val id: Int,
    val title: String,
    val overview: String,
    @SerialName("poster_path") val posterPath: String,
    @SerialName("release_date") val releaseDate: String,
    val runtime: Int,
    @SerialName("vote_average") val voteAverage: Double,
    val genres: List<MovieGenre> = emptyList()
)

@Serializable
data class MovieGenre(
    val name: String
)

    @Serializable
    data class TmdbError(
        @SerialName("status_message") val statusMessage: String = "TMDB request failed.",
    )
