package com.uniquindio.thecatapp.features.list

import com.uniquindio.thecatapp.domain.model.Cat
import com.uniquindio.thecatapp.domain.model.CatBreed
import com.uniquindio.thecatapp.domain.model.CatCategory

data class CatListUiState(
    val cats: List<Cat> = emptyList(),
    val breeds: List<CatBreed> = emptyList(),
    val categories: List<CatCategory> = emptyList(),
    val selectedBreedId: String? = null,
    val selectedCategoryId: Int? = null,
    val isLoading: Boolean = false,
    val isOnline: Boolean = true,
    val errorMessage: String? = null
)

