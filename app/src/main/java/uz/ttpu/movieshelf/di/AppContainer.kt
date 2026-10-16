package uz.ttpu.movieshelf.di

import android.content.Context
import uz.ttpu.movieshelf.data.local.MovieLocalDataSource
import uz.ttpu.movieshelf.data.local.SharedPrefsMovieLocalDataSource
import uz.ttpu.movieshelf.data.remote.FakeMovieRemoteDataSource
import uz.ttpu.movieshelf.data.repository.MovieRepositoryImpl
import uz.ttpu.movieshelf.domain.repository.MovieRepository
import uz.ttpu.movieshelf.domain.usecase.GetFavoriteMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.GetMoviesUseCase
import uz.ttpu.movieshelf.domain.usecase.ToggleFavoriteUseCase

class AppContainer(context: Context) {
    val fakeRemote = FakeMovieRemoteDataSource()
    private val local: MovieLocalDataSource = SharedPrefsMovieLocalDataSource(context)
    private val repository: MovieRepository = MovieRepositoryImpl(fakeRemote, local)

    val getMovies = GetMoviesUseCase(repository)
    val toggleFavorite = ToggleFavoriteUseCase(repository)
    val getFavoriteMovies = GetFavoriteMoviesUseCase(getMovies)
}
