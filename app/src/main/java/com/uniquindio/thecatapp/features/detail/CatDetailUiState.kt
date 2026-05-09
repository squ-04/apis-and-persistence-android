package com.uniquindio.thecatapp.features.detail

import com.uniquindio.thecatapp.domain.model.CatImage

data class CatDetailUiState(
	val isLoading: Boolean = false,
	val isOnline: Boolean = true,
	val detail: CatImage? = null,
	val errorMessage: String? = null,
	val isFavorite: Boolean = false,
	// local DB id for the favorite row (Long) when created offline
	val favoriteLocalId: Long? = null,
	// server id for the favorite resource (Int) when synced
	val favoriteServerId: Int? = null,
	// status e.g. PENDING_ADD, SYNCED, PENDING_DELETE, DELETED
	val favoriteStatus: String? = null,
	val isFavoriteLoading: Boolean = false
)



