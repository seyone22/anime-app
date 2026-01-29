package com.seyone22.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.seyone22.core.model.Anime
import com.seyone22.core.model.sampleTrendingAnime

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onAnimeClick: (Int) -> Unit = {}
) {
    // Surface handles the background color automatically based on the theme
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            contentPadding = PaddingValues(bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // 1. Header Title
            item {
                HomeHeader()
            }

            // 2. Hero Section (Featured)
            item {
                HeroSection(
                    anime = sampleTrendingAnime.first(),
                    onClick = onAnimeClick
                )
            }

            // 3. Trending Carousel
            item {
                AnimeSection(
                    title = "Trending Now",
                    animeList = sampleTrendingAnime,
                    onAnimeClick = onAnimeClick
                )
            }

            // 4. Seasonal Carousel
            item {
                AnimeSection(
                    title = "Winter 2026 Season",
                    animeList = sampleTrendingAnime.shuffled(),
                    onAnimeClick = onAnimeClick
                )
            }
        }
    }
}

@Composable
fun HomeHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Explore",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "What are you watching today?",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun HeroSection(anime: Anime, onClick: (Int) -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(380.dp) // Immersive height
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(32.dp)) // Material 3 Expressive Shape
            .clickable { onClick(anime.id) }
            .background(MaterialTheme.colorScheme.surfaceVariant) // Placeholder color
    ) {
        // Image Placeholder (Replace with actual image URL in production)
        AsyncImage(
            model = anime.coverUrl,
            contentDescription = anime.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Overlay for text readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f)),
                        startY = 300f
                    )
                )
        )

        // Hero Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(24.dp)
        ) {
            SuggestionChip(
                onClick = {},
                label = { Text("New Episode") },
                colors = SuggestionChipDefaults.suggestionChipColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    labelColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = null
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = anime.title,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFD700),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${anime.rating}% Match",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // Play Button FAB-like action
        FloatingActionButton(
            onClick = { onClick(anime.id) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = "Play")
        }
    }
}

@Composable
fun AnimeSection(
    title: String,
    animeList: List<Anime>,
    onAnimeClick: (Int) -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(animeList) { anime ->
                AnimePosterCard(anime, onAnimeClick)
            }
        }
    }
}

@Composable
fun AnimePosterCard(anime: Anime, onClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clickable { onClick(anime.id) }
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .height(220.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            AsyncImage(
                model = anime.coverUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = anime.title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = anime.season,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview
@Composable
fun HomePreview() {
    MaterialTheme {
        HomeScreen()
    }
}