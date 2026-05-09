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
			val favEntity = repository.getFavoriteForImage(catId)
			_uiState.update {
				it.copy(
					isFavorite = favEntity?.status?.let { s -> s != "DELETED" } == true,
					favoriteLocalId = favEntity?.localId,
					favoriteServerId = favEntity?.id,
					favoriteStatus = favEntity?.status
				)
			}
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
		val state = _uiState.value
		viewModelScope.launch {
			_uiState.update { it.copy(isFavoriteLoading = true) }

			// If currently not favorite -> add
			if (!state.isFavorite) {
				if (state.isOnline) {
					repository.addFavorite(catId)
						.onSuccess { newFavId ->
							// ensure local record is marked synced
							val favEntity = repository.getFavoriteForImage(catId)
							_uiState.update {
								it.copy(
									isFavorite = true,
									favoriteServerId = newFavId,
									favoriteLocalId = favEntity?.localId,
									favoriteStatus = "SYNCED",
									isFavoriteLoading = false
								)
							}
						}
						.onFailure { error ->
							_uiState.update { it.copy(isFavoriteLoading = false, errorMessage = error.message ?: "No se pudo agregar favorito") }
						}
				} else {
					// offline: add pending local favorite
					repository.addFavoriteOffline(catId)
						.onSuccess { localId ->
							_uiState.update { it.copy(isFavorite = true, favoriteLocalId = localId, favoriteServerId = null, favoriteStatus = "PENDING_ADD", isFavoriteLoading = false) }
						}
						.onFailure { error ->
							_uiState.update { it.copy(isFavoriteLoading = false, errorMessage = error.message ?: "No se pudo agregar favorito localmente") }
						}
				}
			} else {
				// currently favorite -> remove
				val localId = state.favoriteLocalId
				val serverId = state.favoriteServerId

				if (state.isOnline && serverId != null) {
					repository.removeFavorite(serverId)
						.onSuccess {
							// update local state: mark deleted
							val favEntity = repository.getFavoriteForImage(catId)
							_uiState.update { it.copy(isFavorite = false, favoriteLocalId = favEntity?.localId, favoriteServerId = favEntity?.id, favoriteStatus = favEntity?.status, isFavoriteLoading = false) }
						}
						.onFailure { error ->
							_uiState.update { it.copy(isFavoriteLoading = false, errorMessage = error.message ?: "No se pudo eliminar favorito") }
						}
				} else if (localId != null) {
					// offline removal of local record
					repository.removeFavoriteLocal(localId)
						.onSuccess {
							_uiState.update { it.copy(isFavorite = false, favoriteStatus = "PENDING_DELETE", isFavoriteLoading = false) }
						}
						.onFailure { error ->
							_uiState.update { it.copy(isFavoriteLoading = false, errorMessage = error.message ?: "No se pudo marcar favorito para eliminar") }
						}
				} else {
					// fallback: mark not favorite locally
					_uiState.update { it.copy(isFavorite = false, favoriteLocalId = null, favoriteServerId = null, favoriteStatus = null, isFavoriteLoading = false) }
				}
			}
		}
	}

	private fun observeConnection() {
		viewModelScope.launch {
			connectivityObserver.isOnline.collect { online ->
				_uiState.update { it.copy(isOnline = online) }
				if (online) {
					// try to sync any pending favorites when back online
					launch {
						repository.syncPendingFavorites()
					}
				}
			}
		}
	}
}