    val createdAt: Long
package com.uniquindio.thecatapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
    @PrimaryKey(autoGenerate = true) val localId: Long = 0L,
    val serverId: Int? = null,
    @PrimaryKey val id: Int,
    val status: String, // PENDING_ADD, SYNCED, PENDING_DELETE
    val createdAt: Long,
    val updatedAt: Long
    val createdAt: Long
)

