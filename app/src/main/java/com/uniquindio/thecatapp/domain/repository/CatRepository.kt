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

    // Favoritos
    suspend fun addFavorite(imageId: String): Result<Int>

    suspend fun removeFavorite(favouriteId: Int): Result<Unit>

    suspend fun getFavoriteIdForImage(imageId: String): Int?

    // Offline-first favorites
    suspend fun getFavoriteForImage(imageId: String): com.uniquindio.thecatapp.data.local.entity.FavoriteEntity?

    suspend fun addFavoriteOffline(imageId: String): Result<Long>

    suspend fun removeFavoriteLocal(localId: Long): Result<Unit>

    suspend fun syncPendingFavorites(): Result<Unit>
