package com.seyone22.feature.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seyone22.core.data.repository.AnimeRepository
import com.seyone22.core.model.Anime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface DetailsUiState {
    data object Loading : DetailsUiState
    data class Success(val anime: Anime) : DetailsUiState
    data class Error(val message: String) : DetailsUiState
}

class DetailsViewModel(
    private val repository: AnimeRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DetailsUiState>(DetailsUiState.Loading)
    val uiState: StateFlow<DetailsUiState> = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetailsUiState.Loading
    )

    fun loadAnime(id: Int) {
        viewModelScope.launch {
            _uiState.value = DetailsUiState.Loading
            repository.getAnimeDetails(id).collect { result ->
                result.onSuccess { anime ->
                    _uiState.value = DetailsUiState.Success(anime)
                }.onFailure { error ->
                    _uiState.value = DetailsUiState.Error(error.message ?: "Unknown error")
                }
            }
        }
    }
}