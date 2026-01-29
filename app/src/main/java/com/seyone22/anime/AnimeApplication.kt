package com.seyone22.anime

import android.app.Application

class AnimeApplication : Application() {
    // Instance of AppContainer that will be used by the rest of the app
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Initialize the container manually
        container = AppDataContainer(this)
    }
}