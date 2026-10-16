package uz.ttpu.movieshelf.data.remote

// All data-source functions use suspend because I/O operations (network requests,
// disk reads) must not block the main (UI) thread. suspend lets the caller run
// them inside a coroutine on a background dispatcher.

data class MovieDto(
    val id: Int,
    val title: String,
    val releaseYear: Int,
    val score: Double,
)

interface MovieRemoteDataSource {
    suspend fun fetchMovies(): List<MovieDto>
}
