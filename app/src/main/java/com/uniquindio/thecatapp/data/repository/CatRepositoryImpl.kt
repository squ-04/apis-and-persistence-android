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

	override suspend fun addFavorite(imageId: String): Result<Int> {
		return runCatching {
			val response = apiService.addFavorite(com.uniquindio.thecatapp.data.remote.FavouriteRequestDto(image_id = imageId))
			val favId = response.id ?: throw IllegalStateException("No se recibió id de favorito")
			val now = System.currentTimeMillis()
			val existing = favoriteDao.getByImageId(imageId)
			if (existing != null) {
				// update existing local record with server id and mark as SYNCED
				favoriteDao.updateServerIdAndStatus(existing.localId, favId, "SYNCED", now)
			} else {
				val entity = com.uniquindio.thecatapp.data.local.entity.FavoriteEntity(
					id = favId,
					imageId = imageId,
					status = "SYNCED",
					createdAt = now,
					updatedAt = now
				)
				favoriteDao.insert(entity)
			}
			favId
		}.recoverCatching { networkError ->
			// if offline or network error, return existing server id if present, otherwise propagate
			val existing = favoriteDao.getByImageId(imageId)
			if (existing != null) {
				existing.id ?: throw networkError
			} else {
				throw networkError
			}
		}
	}

	override suspend fun removeFavorite(favouriteId: Int): Result<Unit> {
		return try {
			apiService.removeFavorite(favouriteId)
			// mark as DELETED in local DB (keep record)
			val now = System.currentTimeMillis()
			runCatching { favoriteDao.updateStatusByServerId(favouriteId, "DELETED", now) }
			Result.success(Unit)
		} catch (_: Exception) {
			// network failed: mark any local record with this server id as PENDING_DELETE
			val now = System.currentTimeMillis()
			runCatching { favoriteDao.updateStatusByServerId(favouriteId, "PENDING_DELETE", now) }
			Result.success(Unit)
		}
	}

	override suspend fun getFavoriteIdForImage(imageId: String): Int? {
		return favoriteDao.getByImageId(imageId)?.id
	}

	override suspend fun getFavoriteForImage(imageId: String): com.uniquindio.thecatapp.data.local.entity.FavoriteEntity? {
		return favoriteDao.getByImageId(imageId)
	}

	override suspend fun addFavoriteOffline(imageId: String): Result<Long> {
		return runCatching {
			val now = System.currentTimeMillis()
			val existing = favoriteDao.getByImageId(imageId)
			if (existing != null) {
				// If there is an existing record, avoid inserting duplicates.
				// If it was deleted or pending delete, mark it as pending add so it will be synced.
				when (existing.status) {
					"PENDING_ADD", "SYNCED" -> {
						// already pending or synced -> return existing local id
						existing.localId
					}
					else -> {
						// PENDING_DELETE, DELETED, or unknown -> mark as PENDING_ADD
						favoriteDao.updateStatus(existing.localId, "PENDING_ADD", now)
						existing.localId
					}
				}
			} else {
				val entity = com.uniquindio.thecatapp.data.local.entity.FavoriteEntity(
					imageId = imageId,
					status = "PENDING_ADD",
					createdAt = now,
					updatedAt = now
				)
				favoriteDao.insert(entity)
			}
		}
	}

	override suspend fun removeFavoriteLocal(localId: Long): Result<Unit> {
		return runCatching {
			val now = System.currentTimeMillis()
			favoriteDao.updateStatus(localId, "PENDING_DELETE", now)
		}
	}

	override suspend fun syncPendingFavorites(): Result<Unit> {
		return runCatching {
			val pending = favoriteDao.getPending()
			for (item in pending) {
				when (item.status) {
					"PENDING_ADD" -> {
						try {
							val resp = apiService.addFavorite(com.uniquindio.thecatapp.data.remote.FavouriteRequestDto(image_id = item.imageId))
							val serverId = resp.id ?: continue
							favoriteDao.updateServerIdAndStatus(item.localId, serverId, "SYNCED", System.currentTimeMillis())
						} catch (_: Exception) {
							// keep pending
						}
					}
					"PENDING_DELETE" -> {
						if (item.id != null) {
							try {
								apiService.removeFavorite(item.id)
								favoriteDao.updateStatus(item.localId, "DELETED", System.currentTimeMillis())
							} catch (_: Exception) {
								// keep pending delete
							}
						} else {
							// no server id yet, simply mark as DELETED
							favoriteDao.updateStatus(item.localId, "DELETED", System.currentTimeMillis())
						}
					}
					else -> {
						// unknown status - ignore
					}
				}
			}
		}

					override suspend fun clearAllFavorites(): Result<Unit> {
						return runCatching {
							val all = favoriteDao.getAll()
							for (item in all) {
								try {
									// if we have a server id, attempt delete on server
									if (item.id != null) {
										apiService.removeFavorite(item.id)
									}
								} catch (_: Exception) {
									// ignore individual failures - best effort
								}
							}
							// now clear local table entirely
							favoriteDao.deleteAll()
						}
					}
	}
}
