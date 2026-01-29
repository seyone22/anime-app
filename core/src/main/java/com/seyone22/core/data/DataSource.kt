package com.seyone22.core.data

import com.seyone22.core.model.Anime

interface DataSource {
    /**
     * Fetches the top trending anime right now.
     */
    suspend fun getTrendingAnime(): Result<List<Anime>>

    /**
     * Fetches anime for a specific season (e.g., WINTER 2026).
     */
    suspend fun getSeasonalAnime(season: AnimeSeason, year: Int): Result<List<Anime>>

    /**
     * Fetches detailed metadata for a specific anime ID.
     */
    suspend fun getAnimeDetails(id: Int): Result<Anime>

    /**
     * Fetches the airing schedule for a specific range of timestamps.
     * Useful for the "Daily Brief" card.
     */
    suspend fun getAiringSchedule(start: Long, end: Long): Result<List<Anime>>

    /**
     * Get recommendations based on an anime ID.
     */
    suspend fun getRecommendations(animeId: Int): Result<List<Anime>>
}

enum class AnimeSeason {
    WINTER, SPRING, SUMMER, FALL
}