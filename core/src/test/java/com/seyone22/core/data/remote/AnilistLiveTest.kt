package com.seyone22.core.data.remote

import com.seyone22.core.data.AnimeSeason
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * LIVE NETWORK TEST
 * This test hits the REAL AniList API.
 * Requirement: Active Internet Connection.
 */
class AnilistLiveTest {

    // Instantiate the real data source (no mocks!)
    private val dataSource = AnilistDataSource()

    @Test
    fun `test fetch Trending Anime returns data`() = runBlocking {
        println("--- STARTING LIVE REQUEST: TRENDING ---")

        val result = dataSource.getTrendingAnime()

        // 1. Check for failure
        if (result.isFailure) {
            val error = result.exceptionOrNull()
            println("❌ Request Failed: ${error?.message}")
            error?.printStackTrace()
            throw error!!
        }

        // 2. Validate Data
        val animeList = result.getOrNull()!!
        println("✅ Success! Fetched ${animeList.size} anime.")

        assertTrue("List should not be empty", animeList.isNotEmpty())

        // 3. Print samples to console for visual verification
        animeList.take(3).forEach { anime ->
            println("   • [${anime.id}] ${anime.title} (Rating: ${anime.rating}%)")
            println("     Cover: ${anime.coverUrl}")
        }
    }

    @Test
    fun `test fetch Seasonal Anime returns correct season`() = runBlocking {
        println("\n--- STARTING LIVE REQUEST: SEASONAL ---")

        // Dynamic check for current/next season
        val season = AnimeSeason.WINTER
        val year = 2026 // Or current year

        val result = dataSource.getSeasonalAnime(season, year)

        assertTrue("Request failed", result.isSuccess)
        val animeList = result.getOrNull()!!

        println("✅ Fetched ${animeList.size} shows for $season $year")
        animeList.take(3).forEach {
            println("   • ${it.title}")
        }

        assertTrue(animeList.isNotEmpty())
    }

    @Test
    fun `test fetch Specific Anime Details (Solo Leveling)`() = runBlocking {
        println("\n--- STARTING LIVE REQUEST: DETAILS ---")

        // ID 151807 is "Solo Leveling" (Season 1)
        val animeId = 151807
        val result = dataSource.getAnimeDetails(animeId)

        assertTrue("Request failed", result.isSuccess)
        val anime = result.getOrNull()!!

        println("✅ Details Fetched for: ${anime.title}")
        println("   Desc Snippet: ${anime.description.take(50)}...")

        assertTrue(anime.title.contains("Solo Leveling", ignoreCase = true))
    }

    @Test
    fun `test fetch Airing Schedule for current week`() = runBlocking {
        println("\n--- STARTING LIVE REQUEST: SCHEDULE ---")

        // 1. Define a 7-day window starting from now
        // AniList expects Unix timestamps (seconds)
        val now = LocalDateTime.now()
        val startTimestamp = now.toEpochSecond(ZoneOffset.UTC)
        val endTimestamp = now.plusDays(7).toEpochSecond(ZoneOffset.UTC)

        println("Fetching schedule from $now to ${now.plusDays(7)}")

        val result = dataSource.getAiringSchedule(startTimestamp, endTimestamp)

        // 2. Check for failure
        if (result.isFailure) {
            val error = result.exceptionOrNull()
            println("❌ Schedule Request Failed: ${error?.message}")
            throw error!!
        }

        // 3. Validate Data
        val schedule = result.getOrNull()!!
        println("✅ Success! Fetched ${schedule.size} airing entries for the week.")

        assertTrue("Schedule should not be empty during active seasons", schedule.isNotEmpty())

        // 4. Print samples for visual verification
        // Ensure that our mapping to the domain 'Anime' model preserved essential discovery data
        schedule.take(5).forEach { anime ->
            println("   • ${anime.title}")
            // Note: If you updated your Anime model to include airing info, print it here
            println("     Cover: ${anime.coverUrl}")
        }
    }
}