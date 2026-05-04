package com.uniquindio.thecatapp.data.remote

import com.uniquindio.thecatapp.data.remote.dto.CatBreedDto
import com.uniquindio.thecatapp.data.remote.dto.CatCategoryDto
import com.uniquindio.thecatapp.data.remote.dto.CatImageDto

interface CatApiService {

    @GET("images/search")
    suspend fun searchImages(
        @Query("size") size: String = "med",
        @Query("mime_types") mimeTypes: String = "jpg,png",
        @Query("format") format: String = "json",
        @Query("order") order: String = "ASC",
        @Query("page") page: Int,
        @Query("limit") limit: Int = 20,
        @Query("breed_ids") breedIds: String? = null,
        @Query("category_ids") categoryIds: String? = null,
        @Query("include_breeds") includeBreeds: Int = 1,
        @Query("include_categories") includeCategories: Int = 1
    ): List<CatImageDto>

    @GET("images/{imageId}")
    suspend fun getImageById(
        @Path("imageId") imageId: String
    ): CatImageDto

    @GET("breeds")
    suspend fun getBreeds(): List<CatBreedDto>

    @GET("categories")
    suspend fun getCategories(): List<CatCategoryDto>
}