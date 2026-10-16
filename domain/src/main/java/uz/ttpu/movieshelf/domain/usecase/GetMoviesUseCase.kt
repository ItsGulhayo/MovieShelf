package uz.ttpu.movieshelf.domain.usecase

import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.repository.MovieRepository

class GetMoviesUseCase(private val repository: MovieRepository) {

    suspend operator fun invoke(): MoviesResult {
        val result = repository.getMovies()
        val sorted = result.movies.sortedWith(
            compareByDescending<uz.ttpu.movieshelf.domain.model.Movie> { it.rating }
                .thenBy { it.title }
        )
        return result.copy(movies = sorted)
    }
}
