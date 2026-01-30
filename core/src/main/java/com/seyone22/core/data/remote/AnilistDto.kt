package com.seyone22.core.data.remote

import kotlinx.serialization.Serializable

// The wrapper for all GraphQL responses
@Serializable
data class AnilistResponse<T>(
    val data: T
)

@Serializable
data class PageData(
    val Page: PageContent
)

@Serializable
data class PageContent(
    val media: List<AnilistMediaDto>
)

@Serializable
data class AnilistMediaDto(
    val id: Int,
    val title: AnilistTitle,
    val coverImage: AnilistCover,
    val averageScore: Int? = 0,
    val description: String? = "",
    val bannerImage: String? = null,
    val status: String? = null,
    val nextAiringEpisode: AnilistAiring? = null,
    val season: String? = "",
    val seasonYear: String? = ""
)

@Serializable
data class AnilistTitle(
    val romaji: String,
    val english: String? = null,
    val native: String? = null
)

@Serializable
data class AnilistCover(
    val extraLarge: String,
    val large: String
)

@Serializable
data class AnilistAiring(
    val airingAt: Long,
    val episode: Int
)

// Helper to create GraphQL queries
@Serializable
data class GraphQLRequest(
    val query: String,
    val variables: Map<String, String> = emptyMap()
)

@Serializable
data class AiringSchedulePageData(
    val Page: AiringSchedulePageContent
)

@Serializable
data class AiringSchedulePageContent(
    val airingSchedule: List<AiringScheduleEntryDto>
)

@Serializable
data class AiringScheduleEntryDto(
    val id: Int,
    val episode: Int,
    val airingAt: Long,
    val media: AnilistMediaDto // The actual anime data
)