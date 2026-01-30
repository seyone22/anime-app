package com.seyone22.anime

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.seyone22.feature.details.DetailsViewModel
import com.seyone22.feature.home.HomeViewModel

object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for HomeViewModel
        initializer {
            HomeViewModel(
                repository = animeApplication().container.repository
            )
        }
        initializer {
            DetailsViewModel(
                repository = animeApplication().container.repository
            )
        }
    }
}

/**
 * Extension function to queries for [Application] object and returns an instance of
 * [AnimeApplication].
 */
fun CreationExtras.animeApplication(): AnimeApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as AnimeApplication)