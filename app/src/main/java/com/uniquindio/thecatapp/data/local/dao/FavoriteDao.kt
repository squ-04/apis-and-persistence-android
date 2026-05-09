package com.uniquindio.thecatapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uniquindio.thecatapp.data.local.entity.FavoriteEntity

@Dao
interface FavoriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: FavoriteEntity): Long

    @Query("SELECT * FROM favorites WHERE imageId = :imageId LIMIT 1")
    suspend fun getByImageId(imageId: String): FavoriteEntity?

    @Query("SELECT * FROM favorites WHERE status != 'SYNCED'")
    suspend fun getPending(): List<FavoriteEntity>

    @Query("SELECT * FROM favorites")
    suspend fun getAll(): List<FavoriteEntity>

    @Query("DELETE FROM favorites")
    suspend fun deleteAll()

    @Query("UPDATE favorites SET id = :serverId, status = :status, updatedAt = :updatedAt WHERE localId = :localId")
    suspend fun updateServerIdAndStatus(localId: Long, serverId: Int, status: String, updatedAt: Long)

    @Query("UPDATE favorites SET status = :status, updatedAt = :updatedAt WHERE localId = :localId")
    suspend fun updateStatus(localId: Long, status: String, updatedAt: Long)

    @Query("UPDATE favorites SET status = :status, updatedAt = :updatedAt WHERE id = :serverId")
    suspend fun updateStatusByServerId(serverId: Int, status: String, updatedAt: Long)
}

