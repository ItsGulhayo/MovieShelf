package uz.ttpu.movieshelf.domain.usecase

import uz.ttpu.movieshelf.domain.model.MoviesResult

class GetFavoriteMoviesUseCase(private val getMovies: GetMoviesUseCase) {

    suspend operator fun invoke(): MoviesResult {
        val result = getMovies()
        val favorites = result.movies.filter { it.isFavorite }
        return result.copy(movies = favorites)
    }
}
