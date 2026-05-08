package com.uniquindio.thecatapp.features.detail

import com.uniquindio.thecatapp.domain.model.CatImage

data class CatDetailUiState(
	val isLoading: Boolean = false,
	val isOnline: Boolean = true,
	val detail: CatImage? = null,
	val errorMessage: String? = null,
	val isFavorite: Boolean = false,
	val favoriteLocalId: Long? = null,
	val favoriteServerId: Int? = null,
	val favoriteStatus: String? = null,
	val isFavoriteLoading: Boolean = false
)



