package com.uniquindio.thecatapp.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
	tableName = "cat_images",
	primaryKeys = ["queryKey", "page", "catId"],
	indices = [
		Index(value = ["queryKey", "page", "itemOrder"])
	]
)
data class CatImageEntity(
	val queryKey: String,
	val page: Int,
	val itemOrder: Int,
	val catId: String,
	val url: String,
	val breedId: String?,
	val categoryId: Int?,
	val updatedAt: Long
)
