package uz.ttpu.movieshelf

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import uz.ttpu.movieshelf.presentation.movies.MovieListScreen
import uz.ttpu.movieshelf.presentation.movies.MovieListViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = (application as MovieShelfApp).container

        setContent {
            MaterialTheme {
                val viewModel: MovieListViewModel = viewModel(
                    factory = viewModelFactory {
                        initializer {
                            MovieListViewModel(
                                container.getMovies,
                                container.toggleFavorite,
                                container.getFavoriteMovies,
                            )
                        }
                    }
                )
                val state by viewModel.state.collectAsStateWithLifecycle()
                var online by remember { mutableStateOf(container.fakeRemote.isOnline) }

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(Modifier.padding(innerPadding)) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text("Fake network online", Modifier.weight(1f))
                            Switch(
                                checked = online,
                                onCheckedChange = {
                                    online = it
                                    container.fakeRemote.isOnline = it
                                },
                            )
                        }
                        MovieListScreen(
                            state = state,
                            onFavoriteClick = viewModel::onFavoriteClick,
                            onRefresh = viewModel::onRefresh,
                            onFavoritesOnlyChange = viewModel::onFavoritesOnlyChange,
                        )
                    }
                }
            }
        }
    }
}
