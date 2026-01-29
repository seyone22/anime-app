package com.seyone22.core.model

data class Anime(
    val id: Int,
    val title: String,
    val coverUrl: String,
    val rating: Int,
    val description: String = "",
    val season: String = ""
)

// Mock Data for Phase 1 Prototyping
val sampleTrendingAnime = listOf(
    Anime(1, "Solo Leveling Season 2", "https://placeholder.com/1", 92, "Arise.", "Winter 2026"),
    Anime(2, "Sakamoto Days", "https://placeholder.com/2", 88, "Legendary hitman.", "Winter 2026"),
    Anime(3, "Frieren: Beyond Journey's End", "https://placeholder.com/3", 95, "Magic and time.", "Past"),
    Anime(4, "One Piece", "https://placeholder.com/4", 90, "Pirate King.", "Ongoing"),
    Anime(5, "Chainsaw Man Movie", "https://placeholder.com/5", 89, "Reze arc.", "Coming Soon"),
)