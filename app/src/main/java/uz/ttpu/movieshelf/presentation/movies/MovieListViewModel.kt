package uz.ttpu.movieshelf.presentation.movies

/*
 Task 15 — Trace one action through all layers (analysis):

 (1) Order of calls when a heart is tapped:
     UI (MovieItem onClick) → MovieListViewModel.onFavoriteClick
     → ToggleFavoriteUseCase.invoke → MovieRepositoryImpl.toggleFavorite
     → SharedPrefsMovieLocalDataSource.setFavorite

 (2) Dependency arrows between the five classes:
     MovieItem → MovieListViewModel → ToggleFavoriteUseCase → MovieRepository (interface)
     MovieRepositoryImpl → MovieRepository (implements it)
     MovieRepositoryImpl → SharedPrefsMovieLocalDataSource (via MovieLocalDataSource interface)

     The arrow between ToggleFavoriteUseCase and MovieRepositoryImpl points the
     opposite way to the call: the use case calls the repository, but the
     source-code dependency points from the repository to the interface that
     lives in the domain. This is Dependency Inversion — the outer layer
     (data) depends on the inner layer (domain), even though at runtime the
     call flows outward.

 (3) The use case cannot call Log.d because the domain module is a pure Kotlin
     module without any Android dependency. The clean way to log from the domain
     would be to declare a Logger interface in the domain layer and implement it
     in an outer layer (e.g., an AndroidLogger in the data or presentation
     layer) — this is Dependency Inversion again.
*/

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.usecase.GetFavoriteMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.ToggleFavoriteUseCase
import java.io.IOException

sealed interface MovieListUiState {
    data object Loading : MovieListUiState
    data class Success(
        val movies: List<Movie>,
        val isFromCache: Boolean,
        val favoritesOnly: Boolean = false,
    ) : MovieListUiState

    data class Error(val message: String) : MovieListUiState
}

class MovieListViewModel(
    private val getMovies: GetMoviesUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
    private val getFavoriteMovies: GetFavoriteMoviesUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow<MovieListUiState>(MovieListUiState.Loading)
    val state: StateFlow<MovieListUiState> = _state.asStateFlow()

    private var favoritesOnly = false

    init {
        load()
    }

    fun onRefresh() = load()

    fun onFavoriteClick(movieId: Int) {
        viewModelScope.launch {
            val newState = toggleFavorite(movieId)
            val current = _state.value
            if (current is MovieListUiState.Success) {
                val updated = current.movies.map { movie ->
                    if (movie.id == movieId) movie.copy(isFavorite = newState)
                    else movie
                }
                val visible = if (favoritesOnly) updated.filter { it.isFavorite } else updated
                _state.value = current.copy(movies = visible)
            }
        }
    }

    fun onFavoritesOnlyChange(enabled: Boolean) {
        favoritesOnly = enabled
        load()
    }

    private fun load() {
        _state.value = MovieListUiState.Loading
        viewModelScope.launch {
            try {
                val result: MoviesResult = if (favoritesOnly) {
                    getFavoriteMovies()
                } else {
                    getMovies()
                }
                _state.value = MovieListUiState.Success(
                    movies = result.movies,
                    isFromCache = result.isFromCache,
                    favoritesOnly = favoritesOnly,
                )
            } catch (_: IOException) {
                _state.value = MovieListUiState.Error(
                    "Can't load movies. Check your connection and try again."
                )
            }
        }
    }
}
