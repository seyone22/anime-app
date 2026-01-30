package com.seyone22.core.data.remote

import com.seyone22.core.data.AnimeSeason
import com.seyone22.core.data.DataSource
import com.seyone22.core.model.Anime
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class AnilistDataSource : DataSource {

    // Initialize Ktor Client
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val ANILIST_URL = "https://graphql.anilist.co"

    override suspend fun getTrendingAnime(): Result<List<Anime>> {
        val query = """
            query {
                Page(page: 1, perPage: 10) {
                    media(sort: TRENDING_DESC, type: ANIME) {
                        id
                        title { romaji english }
                        coverImage { extraLarge large }
                        averageScore
                        description
                    }
                }
            }
        """.trimIndent()

        return executeQuery(query).map { it.map { dto -> dto.toDomain() } }
    }

    override suspend fun getSeasonalAnime(season: AnimeSeason, year: Int): Result<List<Anime>> {
        val query = """
            query {
                Page(page: 1, perPage: 20) {
                    media(season: ${season.name}, seasonYear: $year, type: ANIME, sort: POPULARITY_DESC) {
                        id
                        title { romaji english }
                        coverImage { extraLarge large }
                        averageScore
                    }
                }
            }
        """.trimIndent()

        return executeQuery(query).map { it.map { dto -> dto.toDomain() } }
    }

    override suspend fun getAnimeDetails(id: Int): Result<Anime> {
        val query = """
            query {
                Media(id: $id, type: ANIME) {
                    id
                    title { romaji english }
                    coverImage { extraLarge large }
                    averageScore
                    description
                    bannerImage
                    status
                    season
                    seasonYear
                }
            }
        """.trimIndent()

        // Reuse the generic execution, but handle the single object response logic
        // For simplicity in this snippet, I'll wrap it manually or fetch as a list of 1
        return try {
            val response = client.post(ANILIST_URL) {
                contentType(ContentType.Application.Json)
                setBody(GraphQLRequest(query))
            }.body<AnilistResponse<Map<String, AnilistMediaDto>>>() // Map for single Media root

            val mediaDto = response.data["Media"]!!
            Result.success(mediaDto.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAiringSchedule(start: Long, end: Long): Result<List<Anime>> {
        // Implement schedule query here (using AiringSchedule endpoint)
        // For now, return empty to prevent compilation errors
        return Result.success(emptyList())
    }

    override suspend fun getRecommendations(animeId: Int): Result<List<Anime>> {
        // Implement recommendations query
        return Result.success(emptyList())
    }

    // --- Helper Functions ---

    private suspend fun executeQuery(query: String): Result<List<AnilistMediaDto>> {
        return try {
            val response = client.post(ANILIST_URL) {
                contentType(ContentType.Application.Json)
                setBody(GraphQLRequest(query))
            }.body<AnilistResponse<PageData>>()

            Result.success(response.data.Page.media)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    // Extension function to map DTO to your Core Model
    private fun AnilistMediaDto.toDomain(): Anime {
        return Anime(
            id = this.id,
            title = this.title.english ?: this.title.romaji,
            coverUrl = this.coverImage.extraLarge,
            rating = this.averageScore ?: 0,
            description = this.description ?: "No description available.",
            season = this.season ?: "",
            seasonYear = this.seasonYear ?: ""
        )
    }
}