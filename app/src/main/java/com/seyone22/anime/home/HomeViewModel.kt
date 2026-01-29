package com.seyone22.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seyone22.core.data.repository.AnimeRepository
import com.seyone22.core.model.Anime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// The single state object for the screen
sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(
        val featuredAnime: Anime?,
        val trendingAnime: List<Anime>,
        val seasonalAnime: List<Anime>
    ) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

class HomeViewModel(
    private val repository: AnimeRepository
) : ViewModel() {

    // Internal mutable state
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)

    // Exposed immutable state
    val uiState: StateFlow<HomeUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState.Loading
    )

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading

            try {
                // Fetch data in parallel (conceptually) or sequentially
                // For simplicity, we fetch trending first to get the Featured item
                val trendingResult = repository.getTrendingAnime()
                val seasonalResult = repository.getSeasonalAnime()
                val featuredResult = repository.getFeaturedAnime()

                // Combine flows or collect results.
                // Since our Repo returns Flows of Results, we collect them.
                // NOTE: For this "Thick Client" phase, we'll simplify collecting:

                var trending: List<Anime> = emptyList()
                var seasonal: List<Anime> = emptyList()
                var featured: Anime? = null

                trendingResult.collect { result ->
                    result.onSuccess { trending = it }
                }

                seasonalResult.collect { result ->
                    result.onSuccess { seasonal = it }
                }

                featuredResult.onSuccess { featured = it }

                if (trending.isNotEmpty()) {
                    _uiState.value = HomeUiState.Success(
                        featuredAnime = featured ?: trending.firstOrNull(),
                        trendingAnime = trending,
                        seasonalAnime = seasonal
                    )
                } else {
                    _uiState.value = HomeUiState.Error("Failed to load anime. Check connection.")
                }

            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.localizedMessage ?: "Unknown Error")
            }
        }
    }
}