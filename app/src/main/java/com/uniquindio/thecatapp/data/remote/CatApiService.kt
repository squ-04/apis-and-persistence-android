package com.uniquindio.thecatapp.data.remote

import com.uniquindio.thecatapp.data.remote.dto.CatBreedDto
import com.uniquindio.thecatapp.data.remote.dto.CatCategoryDto
import com.uniquindio.thecatapp.data.remote.dto.CatImageDto

interface CatApiService {
    suspend fun searchImages(
        size: String = "med",
        mimeTypes: String = "jpg,png",
        format: String = "json",
        order: String = "ASC",
        page: Int,
        limit: Int = 20,
        breedIds: String? = null,
        categoryIds: String? = null,
        includeBreeds: Int = 1,
        includeCategories: Int = 1
    ): List<CatImageDto>

    suspend fun getImageById(
        imageId: String
    ): CatImageDto

    suspend fun getBreeds(): List<CatBreedDto>

    suspend fun getCategories(): List<CatCategoryDto>
}