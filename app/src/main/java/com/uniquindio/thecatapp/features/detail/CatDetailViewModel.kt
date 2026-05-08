package com.uniquindio.thecatapp.features.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uniquindio.thecatapp.core.navigation.AppRoutes
import com.uniquindio.thecatapp.core.network.ConnectivityObserver
import com.uniquindio.thecatapp.domain.repository.CatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CatDetailViewModel @Inject constructor(
	private val repository: CatRepository,
	private val connectivityObserver: ConnectivityObserver,
	savedStateHandle: SavedStateHandle
) : ViewModel() {

	private val catId: String = savedStateHandle.get<String>(AppRoutes.CAT_ID_ARG).orEmpty()

	private val _uiState = MutableStateFlow(CatDetailUiState(isLoading = true))
	val uiState: StateFlow<CatDetailUiState> = _uiState.asStateFlow()

	init {
		observeConnection()
		loadDetail()
	}

	// Comprobar estado de favorito al iniciar
	init {
		viewModelScope.launch {
			val favId = repository.getFavoriteIdForImage(catId)
			_uiState.update { it.copy(isFavorite = favId != null, favoriteId = favId) }
		}
	}

	fun retry() {
		loadDetail()
	}

	private fun loadDetail() {
		if (catId.isBlank()) {
			_uiState.update {
				it.copy(
					isLoading = false,
					errorMessage = "No se recibio un identificador valido"
				)
			}
			return
		}
		viewModelScope.launch {
			_uiState.update { it.copy(isLoading = true, errorMessage = null) }

			repository.getCatImageById(catId)
				.onSuccess { image ->
					_uiState.update { it.copy(isLoading = false, detail = image, errorMessage = null) }
				}
				.onFailure { error ->
					_uiState.update {
						it.copy(
							isLoading = false,
							errorMessage = error.message ?: "No se pudo cargar el detalle"
						)
					}
				}
		}
	}

	fun toggleFavorite() {
		val currentFavId = _uiState.value.favoriteId
		viewModelScope.launch {
			_uiState.update { it.copy(isFavoriteLoading = true) }
			if (currentFavId == null) {
				repository.addFavorite(catId)
					.onSuccess { newFavId ->
						_uiState.update { it.copy(isFavorite = true, favoriteId = newFavId, isFavoriteLoading = false) }
					}
					.onFailure { error ->
						_uiState.update { it.copy(isFavoriteLoading = false, errorMessage = error.message ?: "No se pudo agregar favorito") }
					}
			} else {
				repository.removeFavorite(currentFavId)
					.onSuccess {
						_uiState.update { it.copy(isFavorite = false, favoriteId = null, isFavoriteLoading = false) }
					}
					.onFailure { error ->
						_uiState.update { it.copy(isFavoriteLoading = false, errorMessage = error.message ?: "No se pudo eliminar favorito") }
					}
			}
		}
	}

	private fun observeConnection() {
		viewModelScope.launch {
			connectivityObserver.isOnline.collect { online ->
				_uiState.update { it.copy(isOnline = online) }
			}
		}
	}
}