package uz.ttpu.movieshelf.data.repository

import uz.ttpu.movieshelf.data.local.MovieLocalDataSource
import uz.ttpu.movieshelf.data.mapper.toDomain
import uz.ttpu.movieshelf.data.remote.MovieRemoteDataSource
import uz.ttpu.movieshelf.domain.model.MoviesResult
import uz.ttpu.movieshelf.domain.repository.MovieRepository
import java.io.IOException

class MovieRepositoryImpl(
    private val remote: MovieRemoteDataSource,
    private val local: MovieLocalDataSource,
) : MovieRepository {

    override suspend fun getMovies(): MoviesResult {
        val favoriteIds = local.getFavoriteIds()

        return try {
            val dtos = remote.fetchMovies()
            local.saveMovies(dtos)
            val movies = dtos.map { it.toDomain(it.id in favoriteIds) }
            MoviesResult(movies = movies, isFromCache = false)
        } catch (e: IOException) {
            val cached = local.getCachedMovies()
                ?: throw e
            val movies = cached.map { it.toDomain(it.id in favoriteIds) }
            MoviesResult(movies = movies, isFromCache = true)
        }
    }

    override suspend fun toggleFavorite(movieId: Int): Boolean {
        val currentFavorites = local.getFavoriteIds()
        val newState = movieId !in currentFavorites
        local.setFavorite(movieId, newState)
        return newState
    }
}
