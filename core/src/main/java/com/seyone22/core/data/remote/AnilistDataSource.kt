package com.seyone22.core.data.remote

import com.apollographql.apollo.ApolloClient
import com.seyone22.core.data.AnimeSeason
import com.seyone22.core.data.DataSource
import com.seyone22.core.model.Anime

class AnilistDataSource : DataSource {

    private val apolloClient = ApolloClient.Builder()
        .serverUrl("https://graphql.anilist.co")
        .build()

    override suspend fun getTrendingAnime(): Result<List<Anime>> {
        return try {
            val response = apolloClient.query(GetTrendingAnimeQuery(page = com.apollographql.apollo.api.Optional.present(1), perPage = com.apollographql.apollo.api.Optional.present(10))).execute()
            val animeList = response.data?.Page?.media?.filterNotNull()?.map {
                it.animeFields.toDomain()
            } ?: emptyList()
            Result.success(animeList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSeasonalAnime(season: AnimeSeason, year: Int): Result<List<Anime>> {
        return try {
            val apiSeason = MediaSeason.safeValueOf(season.name.uppercase())
            val response = apolloClient.query(
                GetSeasonalAnimeQuery(
                    season = com.apollographql.apollo.api.Optional.present(apiSeason),
                    year = com.apollographql.apollo.api.Optional.present(year),
                    page = com.apollographql.apollo.api.Optional.present(1)
                )
            ).execute()

            val animeList = response.data?.Page?.media?.filterNotNull()?.map {
                it.animeFields.toDomain()
            } ?: emptyList()
            Result.success(animeList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAnimeDetails(id: Int): Result<Anime> {
        return try {
            val response = apolloClient.query(GetAnimeDetailsQuery(id = com.apollographql.apollo.api.Optional.present(id))).execute()
            val media = response.data?.Media
            if (media != null) {
                Result.success(media.animeFields.toDomain())
            } else {
                Result.failure(Exception("Anime with ID $id not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAiringSchedule(start: Long, end: Long): Result<List<Anime>> {
        return try {
            val response = apolloClient.query(
                GetAiringScheduleQuery(
                    start = com.apollographql.apollo.api.Optional.present(start.toInt()),
                    end = com.apollographql.apollo.api.Optional.present(end.toInt())
                )
            ).execute()

            val animeList = response.data?.Page?.airingSchedules?.filterNotNull()?.mapNotNull { entry ->
                entry.media?.animeFields?.toDomain()
            } ?: emptyList()

            Result.success(animeList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getRecommendations(animeId: Int): Result<List<Anime>> {
        return try {
            val response = apolloClient.query(GetAnimeDetailsQuery(id = com.apollographql.apollo.api.Optional.present(animeId))).execute()
            val recommendations = response.data?.Media?.recommendations?.nodes
                ?.filterNotNull()
                ?.mapNotNull { it.mediaRecommendation?.animeFields?.toDomain() }
                ?: emptyList()
            Result.success(recommendations)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun AnimeFields.toDomain(): Anime {
        return Anime(
            id = id,
            title = title?.english ?: title?.romaji ?: "Unknown",
            coverUrl = coverImage?.extraLarge ?: "",
            rating = averageScore ?: 0,
            description = description ?: "No description available.",
            season = season?.name ?: "",
            seasonYear = seasonYear?.toString() ?: ""
        )
    }
}