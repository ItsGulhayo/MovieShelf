package uz.ttpu.movieshelf.presentation.movies

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import uz.ttpu.movieshelf.domain.model.Movie

@Composable
fun MovieListScreen(
    state: MovieListUiState,
    onFavoriteClick: (Int) -> Unit,
    onRefresh: () -> Unit,
    onFavoritesOnlyChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    when (state) {
        is MovieListUiState.Loading -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is MovieListUiState.Error -> {
            Box(modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.message)
                    Button(onClick = onRefresh, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Retry")
                    }
                }
            }
        }

        is MovieListUiState.Success -> {
            Column(modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    FilterChip(
                        selected = state.favoritesOnly,
                        onClick = { onFavoritesOnlyChange(!state.favoritesOnly) },
                        label = { Text("Favorites only") },
                    )
                    TextButton(onClick = onRefresh) {
                        Text("Refresh")
                    }
                }

                if (state.isFromCache) {
                    Text(
                        text = "Offline \u2014 showing saved data",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    )
                }

                LazyColumn {
                    items(state.movies, key = { it.id }) { movie ->
                        MovieItem(
                            movie = movie,
                            onFavoriteClick = { onFavoriteClick(movie.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MovieItem(movie: Movie, onFavoriteClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${movie.year} \u00b7 Rating ${movie.rating}",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            IconButton(onClick = onFavoriteClick) {
                Icon(
                    imageVector = if (movie.isFavorite) Icons.Default.Favorite
                    else Icons.Default.FavoriteBorder,
                    contentDescription = if (movie.isFavorite) "Remove from favorites"
                    else "Add to favorites",
                )
            }
        }
    }
}

private val sampleMovies = listOf(
    Movie(1, "Echoes of Khiva", 2022, 9.0, isFavorite = true),
    Movie(2, "Registan Rhapsody", 2018, 8.4),
    Movie(3, "The Silk Road Express", 2019, 8.4),
)

@Preview(showBackground = true)
@Composable
private fun PreviewLoading() {
    MovieListScreen(
        state = MovieListUiState.Loading,
        onFavoriteClick = {},
        onRefresh = {},
        onFavoritesOnlyChange = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewSuccess() {
    MovieListScreen(
        state = MovieListUiState.Success(sampleMovies, isFromCache = false),
        onFavoriteClick = {},
        onRefresh = {},
        onFavoritesOnlyChange = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewSuccessCached() {
    MovieListScreen(
        state = MovieListUiState.Success(sampleMovies, isFromCache = true),
        onFavoriteClick = {},
        onRefresh = {},
        onFavoritesOnlyChange = {},
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewError() {
    MovieListScreen(
        state = MovieListUiState.Error("Can't load movies. Check your connection and try again."),
        onFavoriteClick = {},
        onRefresh = {},
        onFavoritesOnlyChange = {},
    )
}
