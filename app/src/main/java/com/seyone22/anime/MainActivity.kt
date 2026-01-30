package com.seyone22.anime

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.seyone22.anime.ui.theme.AnimeTheme
import com.seyone22.feature.details.DetailsScreen
import com.seyone22.feature.home.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimeTheme {
                AnimeApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewScreenSizes
@Composable
fun AnimeApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    var selectedAnimeId by rememberSaveable { mutableStateOf<Int?>(null) }

// If we have a selected anime, show Details. Otherwise, show the Navigation Suite.
    if (selectedAnimeId != null) {
        // Details Screen (Full Screen)
        DetailsScreen(
            animeId = selectedAnimeId!!,
            onBackClick = { selectedAnimeId = null } // Go back
        )
    } else {
        // Main Dashboard
        NavigationSuiteScaffold(
            navigationSuiteItems = {
                AppDestinations.entries.forEach {
                    item(
                        icon = { Icon(it.icon, contentDescription = it.label) },
                        label = { Text(it.label) },
                        selected = it == currentDestination,
                        onClick = { currentDestination = it })
                }
            }) {
            Scaffold(
                topBar = {
                        TopAppBar(
                            title = { Text(currentDestination.label) },
                            scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(),
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.Transparent
                            ),
                        )
                },
                modifier = Modifier.fillMaxSize()) { innerPadding ->
                when (currentDestination) {
                    AppDestinations.HOME -> {
                        HomeScreen(
                            modifier = Modifier.padding(innerPadding), onAnimeClick = { id ->
                                selectedAnimeId = id // Trigger navigation
                            })
                    }
                    // ... other destinations
                    else -> {}
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Discover", Icons.Default.Home), FAVORITES(
        "Favorites", Icons.Default.Favorite
    ),
    PROFILE("Profile", Icons.Default.AccountBox),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", modifier = modifier
    )
}