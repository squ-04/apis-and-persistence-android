package com.uniquindio.thecatapp.features.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniquindio.thecatapp.core.network.ConnectivityObserver
import com.uniquindio.thecatapp.domain.repository.CatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CatListViewModel @Inject constructor(
    private val repository: CatRepository,
    private val connectivityObserver: ConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(CatListUiState())
    val uiState: StateFlow<CatListUiState> = _uiState.asStateFlow()

    private var currentPage = 0
    private val limit = 20
    private var isLoadingMore = false

    init {
        observeConnection()
        loadFilters()
        loadCats(reset = true)
    }

    fun loadCats(reset: Boolean = false) {
        if (isLoadingMore) return

        viewModelScope.launch {
            isLoadingMore = true

            try {
                if (reset) {
                    currentPage = 0
                    _uiState.update { it.copy(cats = emptyList()) }
                }

                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val state = _uiState.value

                repository.getCatImages(
                    page = currentPage,
                    limit = limit,
                    breedId = state.selectedBreedId,
                    categoryId = state.selectedCategoryId
                ).onSuccess { newCats ->
                    _uiState.update {
                        it.copy(
                            cats = it.cats + newCats,
                            isLoading = false
                        )
                    }
                    currentPage++
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Error al cargar gatos"
                        )
                    }
                }
            } finally {
                isLoadingMore = false
            }
        }
    }

    fun onBreedSelected(breedId: String?) {
        _uiState.update { it.copy(selectedBreedId = breedId) }
        loadCats(reset = true)
    }

    fun onCategorySelected(categoryId: Int?) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
        loadCats(reset = true)
    }

    private fun observeConnection() {
        viewModelScope.launch {
            connectivityObserver.isOnline.collect { online ->
                _uiState.update { it.copy(isOnline = online) }
            }
        }
    }

    private fun loadFilters() {
        viewModelScope.launch {
            val breeds = repository.getBreeds()
            val categories = repository.getCategories()

            _uiState.update {
                it.copy(
                    breeds = breeds,
                    categories = categories
                )
            }
        }
    }
}