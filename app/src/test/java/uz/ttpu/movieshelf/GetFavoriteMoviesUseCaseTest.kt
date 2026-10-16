package uz.ttpu.movieshelf

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.usecase.GetFavoriteMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase

class GetFavoriteMoviesUseCaseTest {

    @Test
    fun returns_only_favorite_movies() = runBlocking<Unit> {
        val all = MoviesResult(
            movies = listOf(
                Movie(1, "A", 2020, 8.0, isFavorite = true),
                Movie(2, "B", 2021, 7.0, isFavorite = false),
                Movie(3, "C", 2022, 9.1, isFavorite = true),
            ),
            isFromCache = false,
        )
        val getMovies = GetMoviesUseCase(FakeMovieRepository(all))
        val useCase = GetFavoriteMoviesUseCase(getMovies)
        val result = useCase()

        assertEquals(2, result.movies.size)
        assertEquals(listOf("C", "A"), result.movies.map { it.title })
    }
}
