package uz.ttpu.movieshelf

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import uz.ttpu.movieshelf.data.local.InMemoryMovieLocalDataSource
import uz.ttpu.movieshelf.data.remote.MovieDto
import uz.ttpu.movieshelf.data.remote.MovieRemoteDataSource
import uz.ttpu.movieshelf.data.repository.MovieRepositoryImpl
import java.io.IOException

private class FakeRemote(
    var movies: List<MovieDto> = emptyList(),
    var fail: Boolean = false,
) : MovieRemoteDataSource {
    override suspend fun fetchMovies(): List<MovieDto> {
        if (fail) throw IOException("offline")
        return movies
    }
}

class MovieRepositoryImplTest {

    private val dtos = listOf(
        MovieDto(1, "A", 2020, 8.0),
        MovieDto(2, "B", 2021, 7.0),
    )

    @Test
    fun online_returns_fresh_data_with_favorite_flags() = runBlocking<Unit> {
        val local = InMemoryMovieLocalDataSource().also { it.setFavorite(2, true) }
        val repo = MovieRepositoryImpl(FakeRemote(dtos), local)

        val result = repo.getMovies()

        assertFalse(result.isFromCache)
        assertEquals(listOf(false, true), result.movies.map { it.isFavorite })
    }

    @Test
    fun offline_after_a_successful_load_returns_cache() = runBlocking<Unit> {
        val remote = FakeRemote(dtos)
        val local = InMemoryMovieLocalDataSource()
        val repo = MovieRepositoryImpl(remote, local)

        repo.getMovies()

        remote.fail = true
        val result = repo.getMovies()

        assertTrue(result.isFromCache)
        assertEquals(dtos.size, result.movies.size)
    }

    @Test(expected = IOException::class)
    fun offline_with_empty_cache_throws() = runBlocking<Unit> {
        val remote = FakeRemote(fail = true)
        val local = InMemoryMovieLocalDataSource()
        val repo = MovieRepositoryImpl(remote, local)

        repo.getMovies()
    }

    @Test
    fun toggle_returns_new_state() = runBlocking<Unit> {
        val local = InMemoryMovieLocalDataSource()
        val repo = MovieRepositoryImpl(FakeRemote(dtos), local)

        val first = repo.toggleFavorite(1)
        assertTrue(first)

        val second = repo.toggleFavorite(1)
        assertFalse(second)
    }
}
