package com.uniquindio.thecatapp.data.repository

import com.uniquindio.thecatapp.data.local.CatMemoryStore
import com.uniquindio.thecatapp.domain.model.Cat
import com.uniquindio.thecatapp.domain.model.CatBreed
import com.uniquindio.thecatapp.domain.model.CatCategory
import com.uniquindio.thecatapp.domain.repository.CatRepository

import javax.inject.Inject

class CatRepositoryImpl @Inject constructor(
	private val memoryStore: CatMemoryStore
) : CatRepository {

	override suspend fun getCatImages(
		page: Int,
		limit: Int,
		breedId: String?,
		categoryId: Int?
	): Result<List<Cat>> = runCatching {
		memoryStore.getCats(
			page = page,
			limit = limit,
			breedId = breedId,
			categoryId = categoryId
		)
	}

	override suspend fun getBreeds(): List<CatBreed> = memoryStore.getBreeds()

	override suspend fun getCategories(): List<CatCategory> = memoryStore.getCategories()
}
