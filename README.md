# TMDB Movie Explorer

This project was created for my Mobile App Development final project. It lets users browse popular movies, view movie details, and save titles to a watchlist.

## Project walkthrough

The project walkthrough is available [in this video](https://youtu.be/z6xn8AZwZ1I).

## How it works

- The app requests popular movies and movie details from the TMDB API using Ktor.
- Jetpack Compose displays movie posters, release dates, ratings, overviews, runtime, and genres.
- A `ViewModel` exposes screen data as `StateFlow`, which the UI collects with `collectAsStateWithLifecycle()`.
- Users can add movies to their watchlist and remove them later.
- Loading and error states provide feedback while movie data is being retrieved.

## API token

Before running the app, replace the placeholder access token in `MovieRepository.kt` with your TMDB API Read Access Token.