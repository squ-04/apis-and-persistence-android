package com.uniquindio.thecatapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cat_detail")
data class CatDetailEntity(
    @PrimaryKey val catId: String,
    val url: String,
    val width: Int?,
    val height: Int?,
    val mimeType: String?,
    val breedId: String?,
    val breedName: String?,
    val categoryId: Int?,
    val categoryName: String?,
    val updatedAt: Long
)

