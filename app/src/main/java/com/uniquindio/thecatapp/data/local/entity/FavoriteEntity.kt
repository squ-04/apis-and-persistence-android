package com.uniquindio.thecatapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class FavoriteStatus {
    SYNCED,
    PENDING_ADD,
    PENDING_DELETE
}

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey(autoGenerate = true) val localId: Long = 0L,
    val serverId: Int? = null,
    val imageId: String,
    val status: FavoriteStatus = FavoriteStatus.SYNCED,
    val createdAt: Long = System.currentTimeMillis()
)
