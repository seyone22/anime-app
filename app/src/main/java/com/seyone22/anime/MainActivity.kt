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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.seyone22.anime.ui.theme.AnimeTheme
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

@PreviewScreenSizes
@Composable
fun AnimeApp() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = { Icon(it.icon, contentDescription = it.label) },
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        // Use the content padding provided by the navigation suite
        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            // Switch content based on destination
            when(currentDestination) {
                AppDestinations.HOME -> {
                    HomeScreen(
                        modifier = Modifier.padding(innerPadding),
                        // Use the Factory we just created
                        onAnimeClick = { /* ... */ }
                    )
                }
                AppDestinations.FAVORITES -> {
                    // Placeholder
                    Greeting("Favorites", Modifier.padding(innerPadding))
                }
                AppDestinations.PROFILE -> {
                    // Placeholder
                    Greeting("Profile", Modifier.padding(innerPadding))
                }
            }
        }
    }
}

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
) {
    HOME("Home", Icons.Default.Home),
    FAVORITES("Favorites", Icons.Default.Favorite),
    PROFILE("Profile", Icons.Default.AccountBox),
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}