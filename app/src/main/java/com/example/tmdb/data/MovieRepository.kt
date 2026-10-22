package com.example.tmdb.data

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
// bearerAuth es nuevo.
import io.ktor.client.request.bearerAuth

private const val TMDB_ACCESS_TOKEN = "YOUR_TMDB_ACCESS_TOKEN"

class MovieRepository(private val accessToken: String = TMDB_ACCESS_TOKEN) {
    private val baseUrl = "https://api.themoviedb.org/3"

    suspend fun fetchPopularMovies(): Result<List<MovieSummary>> {
        return try {
            val httpResponse = NetworkClient.client.get("$baseUrl/movie/popular") {
                bearerAuth(accessToken)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data = httpResponse.body<MovieCatalogResponse>()
                    Result.success(httpResponse.body<MovieCatalogResponse>().results)
                }
                else -> {
                    val error = httpResponse.body<TmdbError>()
                    Result.failure(Exception("TMDB Error: ${error.statusMessage}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun fetchMovieDetails(movieId: Int): Result<MovieDetails> {
        return try {
            val httpResponse = NetworkClient.client.get("$baseUrl/movie/$movieId") {
                bearerAuth(accessToken)
            }

            when (httpResponse.status) {
                HttpStatusCode.OK -> {
                    val data = httpResponse.body<MovieDetails>()
                    Result.success(data)
                }
                else -> {
                    val error = httpResponse.body<TmdbError>()
                    Result.failure(Exception("TMDB Error: ${error.statusMessage}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
