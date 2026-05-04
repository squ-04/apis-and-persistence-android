package com.uniquindio.thecatapp.domain.repository

import com.uniquindio.thecatapp.domain.model.Cat
import com.uniquindio.thecatapp.domain.model.CatBreed
import com.uniquindio.thecatapp.domain.model.CatCategory
import com.uniquindio.thecatapp.domain.model.CatImage

interface CatRepository {
    suspend fun getCatImages(
        page: Int,
        limit: Int,
        breedId: String?,
        categoryId: Int?
    ): Result<List<Cat>>

    suspend fun getBreeds(): List<CatBreed>

    suspend fun getCategories(): List<CatCategory>

    suspend fun getCatImageById(catId: String): Result<CatImage>
}
