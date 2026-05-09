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
                // No limpiamos la lista inmediatamente para evitar saltos en el contador
                _uiState.update { it.copy(isLoading = true, errorMessage = null) }

                val state = _uiState.value
                val pageToLoad = if (reset) 0 else currentPage

                repository.getCatImages(
                    page = pageToLoad,
                    limit = limit,
                    breedId = state.selectedBreedId,
                    categoryId = state.selectedCategoryId
                ).onSuccess { newCats ->
                    _uiState.update {
                        it.copy(
                            cats = if (reset) newCats else it.cats + newCats,
                            isLoading = false,
                            errorMessage = null,
                            isShowingCachedData = !it.isOnline // Si no hay internet, es cache por definición
                        )
                    }
                    if (newCats.isNotEmpty()) {
                        if (reset) currentPage = 1 else currentPage++
                    } else if (reset) {
                        currentPage = 0
                    }
                }.onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = if (it.cats.isEmpty()) error.message ?: "Error al cargar gatos" else null,
                            isShowingCachedData = true // Falló la API, estamos viendo cache
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
                _uiState.update { currentState ->
                    currentState.copy(
                        isOnline = online,
                        // Si perdemos conexión, es 100% seguro que mostramos local/cache
                        // Si la recuperamos, no marcamos false hasta que hagamos un load exitoso
                        isShowingCachedData = if (!online) true else currentState.isShowingCachedData
                    )
                }

                // Si recuperamos internet y la lista está vacía o estábamos mostrando caché, forzamos recarga
                if (online) {
                    val state = _uiState.value
                    if (state.cats.isEmpty() || state.isShowingCachedData || state.errorMessage != null) {
                        loadCats(reset = true)
                    }
                }
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