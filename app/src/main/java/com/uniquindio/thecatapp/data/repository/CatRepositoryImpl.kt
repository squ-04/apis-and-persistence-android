package com.uniquindio.thecatapp.data.repository

import com.uniquindio.thecatapp.data.local.dao.BreedDao
import com.uniquindio.thecatapp.data.local.dao.CatDetailDao
import com.uniquindio.thecatapp.data.local.dao.CatImageDao
import com.uniquindio.thecatapp.data.local.dao.CategoryDao
import com.uniquindio.thecatapp.data.mapper.toDomain
import com.uniquindio.thecatapp.data.mapper.toDomainImage
import com.uniquindio.thecatapp.data.mapper.toDomainOrNull
import com.uniquindio.thecatapp.data.mapper.toEntity
import com.uniquindio.thecatapp.data.mapper.toDetailEntityOrNull
import com.uniquindio.thecatapp.data.mapper.toEntityOrNull
import com.uniquindio.thecatapp.data.remote.CatApiService
import com.uniquindio.thecatapp.domain.model.Cat
import com.uniquindio.thecatapp.domain.model.CatBreed
import com.uniquindio.thecatapp.domain.model.CatCategory
import com.uniquindio.thecatapp.domain.model.CatImage
import com.uniquindio.thecatapp.domain.repository.CatRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatRepositoryImpl @Inject constructor(
	private val apiService: CatApiService,
	private val catImageDao: CatImageDao,
	private val catDetailDao: CatDetailDao,
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

			val entities = remote.mapIndexedNotNull { index, dto ->
				dto.toEntityOrNull(
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
			val remote = apiService.getBreeds().mapNotNull { it.toDomainOrNull() }

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
			val remote = apiService.getCategories().mapNotNull { it.toDomainOrNull() }

			categoryDao.deleteAll()
			if (remote.isNotEmpty()) {
				categoryDao.insertAll(remote.map { it.toEntity(updatedAt = now) })
			}

			remote
		}.getOrElse {
			categoryDao.getAll().map { it.toDomain() }
		}
	}

	override suspend fun getCatImageById(catId: String): Result<CatImage> {
		return runCatching {
			val now = System.currentTimeMillis()
			val remote = apiService.getImageById(catId)
			val entity = remote.toDetailEntityOrNull(updatedAt = now)
				?: throw IllegalStateException("El detalle de la imagen no contiene datos validos")

			catDetailDao.upsert(entity)
			entity.toDomainImage()
		}.recoverCatching { networkError ->
			val cached = catDetailDao.getById(catId)
			if (cached != null) {
				cached.toDomainImage()
			} else {
				throw networkError
			}
		}
	}

	private fun buildQueryKey(breedId: String?, categoryId: Int?): String {
		val breedPart = breedId ?: "all"
		val categoryPart = categoryId?.toString() ?: "all"
		return "$breedPart|$categoryPart"
	}
}
