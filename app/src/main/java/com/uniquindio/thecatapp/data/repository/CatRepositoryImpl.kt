package com.uniquindio.thecatapp.data.repository

import com.uniquindio.thecatapp.data.local.dao.BreedDao
import com.uniquindio.thecatapp.data.local.dao.CatImageDao
import com.uniquindio.thecatapp.data.local.dao.CategoryDao
import com.uniquindio.thecatapp.data.mapper.toDomain
import com.uniquindio.thecatapp.data.mapper.toEntity
import com.uniquindio.thecatapp.data.remote.CatApiService
import com.uniquindio.thecatapp.domain.model.Cat
import com.uniquindio.thecatapp.domain.model.CatBreed
import com.uniquindio.thecatapp.domain.model.CatCategory
import com.uniquindio.thecatapp.domain.repository.CatRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatRepositoryImpl @Inject constructor(
	private val apiService: CatApiService,
	private val catImageDao: CatImageDao,
	private val breedDao: BreedDao,
	private val categoryDao: CategoryDao
) : CatRepository {

	override suspend fun getCatImages(
		page: Int,
		limit: Int,
		breedId: String?,
		categoryId: Int?
	): Result<List<Cat>> {
		val queryKey = buildQueryKey(breedId = breedId, categoryId = categoryId)

		return runCatching {
			val now = System.currentTimeMillis()
			val remote = apiService.searchImages(
				page = page,
				limit = limit,
				breedIds = breedId,
				categoryIds = categoryId?.toString()
			)

			val entities = remote.mapIndexed { index, dto ->
				dto.toEntity(
					queryKey = queryKey,
					page = page,
					itemOrder = index,
					updatedAt = now
				)
			}

			catImageDao.deletePage(queryKey = queryKey, page = page)
			if (entities.isNotEmpty()) {
				catImageDao.insertAll(entities)
			}

			entities.map { it.toDomain() }
		}.recoverCatching { networkError ->
			val cached = catImageDao.getPage(queryKey = queryKey, page = page)
			if (cached.isNotEmpty()) {
				cached.map { it.toDomain() }
			} else {
				throw networkError
			}
		}
	}

	override suspend fun getBreeds(): List<CatBreed> {
		return runCatching {
			val now = System.currentTimeMillis()
			val remote = apiService.getBreeds().map { it.toDomain() }

			breedDao.deleteAll()
			if (remote.isNotEmpty()) {
				breedDao.insertAll(remote.map { it.toEntity(updatedAt = now) })
			}

			remote
		}.getOrElse {
			breedDao.getAll().map { it.toDomain() }
		}
	}

	override suspend fun getCategories(): List<CatCategory> {
		return runCatching {
			val now = System.currentTimeMillis()
			val remote = apiService.getCategories().map { it.toDomain() }

			categoryDao.deleteAll()
			if (remote.isNotEmpty()) {
				categoryDao.insertAll(remote.map { it.toEntity(updatedAt = now) })
			}

			remote
		}.getOrElse {
			categoryDao.getAll().map { it.toDomain() }
		}
	}

	private fun buildQueryKey(breedId: String?, categoryId: Int?): String {
		val breedPart = breedId ?: "all"
		val categoryPart = categoryId?.toString() ?: "all"
		return "$breedPart|$categoryPart"
	}
}
