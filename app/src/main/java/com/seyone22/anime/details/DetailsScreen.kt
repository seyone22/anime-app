package com.seyone22.feature.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.seyone22.anime.AppViewModelProvider
import com.seyone22.core.model.Anime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailsScreen(
    viewModel: DetailsViewModel = viewModel(factory = AppViewModelProvider.Factory),
    animeId: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Trigger the load when the screen opens
    LaunchedEffect(animeId) {
        viewModel.loadAnime(animeId)
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            // Transparent Top Bar for immersion
            TopAppBar(
                title = {},
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = onBackClick,
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        // Handle State
        when (val state = uiState) {
            is DetailsUiState.Loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            is DetailsUiState.Error -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
            is DetailsUiState.Success -> {
                AnimeDetailContent(
                    anime = state.anime,
                    // Pass the padding but ignore top so image goes behind status bar
                    contentPadding = innerPadding
                )
            }
        }
    }
}

@Composable
fun AnimeDetailContent(
    anime: Anime,
    contentPadding: PaddingValues
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 1. Hero Image Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp) // Tall header
        ) {
            AsyncImage(
                model = anime.coverUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    // "Blur the shit out of it" - 30.dp is quite heavy and looks great
                    // Note: This requires Android 12+. For older versions,
                    // it will simply render the image normally unless using a library like Cloudinary or Coil transformations.
                    .blur(radius = 30.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .alpha(0.6f) // Dim it slightly so it doesn't fight with the foreground
            )


            Row(
                modifier = Modifier.padding(top = 120.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                AsyncImage(
                    model = anime.coverUrl,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 140.dp, height = 200.dp)
                )

                Column(
                ) {
                    Text(
                        text = anime.title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    SuggestionChip(
                        label= { Text("${anime.season.lowercase().replaceFirstChar { it.uppercase() }} ${anime.seasonYear}") },
                        onClick = {}
                    )

                    // Rating Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        val ratingOutOfFive = anime.rating / 20f
                        val fullStars = ratingOutOfFive.toInt()
                        val hasHalfStar = (ratingOutOfFive - fullStars) >= 0.5f

                        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                            repeat(5) { index ->
                                val icon = when {
                                    index < fullStars -> Icons.Default.Star
                                    index == fullStars && hasHalfStar -> Icons.Default.StarHalf
                                    else -> Icons.Default.StarOutline
                                }

                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700), // Anime Gold
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "${anime.rating}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // 2. Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = { /* TODO: Play */ },
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp)
            ) {
                Icon(Icons.Default.PlayArrow, null)
                Spacer(Modifier.width(8.dp))
                Text("Watch Now")
            }
            // Add "Add to List" button here later
        }

        // 3. Synopsis / Description
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Synopsis",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Using SelectionContainer allows users to copy text (nice utility)
            androidx.compose.foundation.text.selection.SelectionContainer {
                Text(
                    text = anime.description.replace(Regex("<.*?>"), ""), // Simple HTML strip
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4
                )
            }
        }

        // Spacer for Navigation Bar
        Spacer(modifier = Modifier.height(100.dp))
    }
}