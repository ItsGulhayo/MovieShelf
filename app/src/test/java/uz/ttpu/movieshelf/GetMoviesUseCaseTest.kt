package uz.ttpu.movieshelf

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.ttpu.movieshelf.domain.model.Movie
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.repository.MovieRepository
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase

class FakeMovieRepository(
    private val result: MoviesResult,
) : MovieRepository {
    override suspend fun getMovies() = result
    override suspend fun toggleFavorite(movieId: Int) = true
}

class GetMoviesUseCaseTest {

    @Test
    fun movies_are_sorted_by_rating_desc_then_title() = runBlocking<Unit> {
        val unsorted = MoviesResult(
            movies = listOf(
                Movie(1, "B", 2020, 8.0),
                Movie(2, "A", 2021, 8.0),
                Movie(3, "C", 2022, 9.1),
            ),
            isFromCache = false,
        )
        val useCase = GetMoviesUseCase(FakeMovieRepository(unsorted))
        val result = useCase()

        assertEquals(
            listOf("C", "A", "B"),
            result.movies.map { it.title },
        )
    }

    @Test
    fun is_from_cache_flag_is_passed_through() = runBlocking<Unit> {
        val cached = MoviesResult(
            movies = listOf(Movie(1, "X", 2020, 7.0)),
            isFromCache = true,
        )
        val useCase = GetMoviesUseCase(FakeMovieRepository(cached))
        val result = useCase()

        assertTrue(result.isFromCache)

        val fresh = MoviesResult(
            movies = listOf(Movie(1, "X", 2020, 7.0)),
            isFromCache = false,
        )
        val useCase2 = GetMoviesUseCase(FakeMovieRepository(fresh))
        val result2 = useCase2()

        assertFalse(result2.isFromCache)
    }
}
