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
	private val categoryDao: CategoryDao,
	private val favoriteDao: com.uniquindio.thecatapp.data.local.dao.FavoriteDao
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

			if (entities.isNotEmpty()) {
				catImageDao.deletePage(queryKey = queryKey, page = page)
				catImageDao.insertAll(entities)
			}

			entities.map { it.toDomain() }
		}.recoverCatching {
			val cached = catImageDao.getPage(queryKey = queryKey, page = page)
			if (cached.isNotEmpty()) {
				cached.map { it.toDomain() }
			} else {
				throw Exception("No hay conexión y no existen datos guardados para este filtro.")
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
				?: throw IllegalStateException("El detalle no contiene datos válidos")

			catDetailDao.upsert(entity)
			entity.toDomainImage()
		}.recoverCatching {
			val cached = catDetailDao.getById(catId)
			cached?.toDomainImage() 
				?: throw Exception("No se puede ver el detalle sin conexión si no se ha abierto previamente.")
		}
	}

	private fun buildQueryKey(breedId: String?, categoryId: Int?): String {
		val breedPart = breedId ?: "all"
		val categoryPart = categoryId?.toString() ?: "all"
		return "$breedPart|$categoryPart"
	}

	override suspend fun toggleFavorite(imageId: String): Result<Unit> {
		val existing = favoriteDao.getByImageId(imageId)

		return if (existing == null) {
			// Add favorite
			runCatching {
				val response = apiService.addFavorite(com.uniquindio.thecatapp.data.remote.FavouriteRequestDto(image_id = imageId))
				val favId = response.id ?: throw IllegalStateException("No server ID")
				favoriteDao.insert(
					com.uniquindio.thecatapp.data.local.entity.FavoriteEntity(
						serverId = favId,
						imageId = imageId,
						status = com.uniquindio.thecatapp.data.local.entity.FavoriteStatus.SYNCED
					)
				)
				Unit
			}.recoverCatching {
				favoriteDao.insert(
					com.uniquindio.thecatapp.data.local.entity.FavoriteEntity(
						imageId = imageId,
						status = com.uniquindio.thecatapp.data.local.entity.FavoriteStatus.PENDING_ADD
					)
				)
				Unit
			}
		} else {
			// Remove favorite
			val serverId = existing.serverId
			if (serverId != null) {
				runCatching {
					apiService.removeFavorite(serverId)
					favoriteDao.deleteByLocalId(existing.localId)
				}.recoverCatching {
					favoriteDao.insert(existing.copy(status = com.uniquindio.thecatapp.data.local.entity.FavoriteStatus.PENDING_DELETE))
					Unit
				}
			} else {
				// Was only local PENDING_ADD, just delete it
				favoriteDao.deleteByLocalId(existing.localId)
				Result.success(Unit)
			}
		}
	}

	override suspend fun getFavoriteIdForImage(imageId: String): Int? {
		return favoriteDao.getByImageId(imageId)?.serverId
	}

	override suspend fun isFavorite(imageId: String): Boolean {
		val fav = favoriteDao.getByImageId(imageId)
		return fav != null && fav.status != com.uniquindio.thecatapp.data.local.entity.FavoriteStatus.PENDING_DELETE
	}

	override suspend fun syncFavorites(): Result<Unit> {
		return runCatching {
			val pending = favoriteDao.getPendingSync()
			pending.forEach { fav ->
				when (fav.status) {
					com.uniquindio.thecatapp.data.local.entity.FavoriteStatus.PENDING_ADD -> {
						val response = apiService.addFavorite(com.uniquindio.thecatapp.data.remote.FavouriteRequestDto(image_id = fav.imageId))
						response.id?.let { favoriteDao.markAsSynced(fav.localId, it) }
					}
					com.uniquindio.thecatapp.data.local.entity.FavoriteStatus.PENDING_DELETE -> {
						fav.serverId?.let {
							apiService.removeFavorite(it)
							favoriteDao.deleteByLocalId(fav.localId)
						}
					}
					else -> {}
				}
			}
		}
	}
}
