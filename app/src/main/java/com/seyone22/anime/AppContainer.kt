package com.seyone22.anime

import android.content.Context
import com.seyone22.core.data.remote.AnilistDataSource
import com.seyone22.core.data.repository.AnimeRepository

/**
 * AppContainer manual dependency injection interface.
 */
interface AppContainer {
    val repository: AnimeRepository
}

/**
 * Implementation of AppContainer that manages the dependencies.
 */
class AppDataContainer(private val context: Context) : AppContainer {

    // 1. Create the DataSource (Networking)
    // We use 'by lazy' so it's only created when first needed
    private val remoteDataSource: AnilistDataSource by lazy {
        AnilistDataSource()
    }

    // 2. Inject DataSource into Repository (Data Logic)
    override val repository: AnimeRepository by lazy {
        AnimeRepository(remoteDataSource)
    }
}