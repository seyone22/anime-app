package com.seyone22.core.data.repository

import com.seyone22.core.data.AnimeSeason
import com.seyone22.core.data.DataSource
import com.seyone22.core.model.Anime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException

class AnimeRepository(
    private val remoteDataSource: DataSource
) {
    // We use Flows to emit data. This makes swapping to Room (Offline-First) easy later.
    fun getTrendingAnime(): Flow<Result<List<Anime>>> = flow {
        // Emit "Success" or "Failure" from the network
        emit(remoteDataSource.getTrendingAnime())
    }

    fun getSeasonalAnime(): Flow<Result<List<Anime>>> = flow {
        // Hardcoded for Phase 1 demo, can be dynamic later
        emit(remoteDataSource.getSeasonalAnime(AnimeSeason.WINTER, 2026))
    }

    // Helper to get a random "Featured" anime from the trending list for the Hero card
    suspend fun getFeaturedAnime(): Result<Anime> {
        val trending = remoteDataSource.getTrendingAnime()
        return if (trending.isSuccess) {
            val list = trending.getOrThrow()
            if (list.isNotEmpty()) Result.success(list.random()) else Result.failure(IOException("Empty list"))
        } else {
            Result.failure(trending.exceptionOrNull() ?: IOException("Unknown error"))
        }
    }
}