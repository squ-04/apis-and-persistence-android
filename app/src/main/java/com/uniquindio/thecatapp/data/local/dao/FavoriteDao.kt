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

    @Query("DELETE FROM favorites WHERE localId = :localId")
    suspend fun deleteByLocalId(localId: Long)

    @Query("DELETE FROM favorites WHERE serverId = :serverId")
    suspend fun deleteByServerId(serverId: Int)

    @Query("SELECT * FROM favorites WHERE imageId = :imageId LIMIT 1")
    suspend fun getByImageId(imageId: String): FavoriteEntity?

    @Query("SELECT * FROM favorites WHERE status != 'SYNCED'")
    suspend fun getPendingSync(): List<FavoriteEntity>

    @Query("UPDATE favorites SET serverId = :serverId, status = 'SYNCED' WHERE localId = :localId")
    suspend fun markAsSynced(localId: Long, serverId: Int)
}

