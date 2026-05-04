package com.uniquindio.thecatapp.domain.model

data class CatImage(
	val id: String,
	val url: String,
	val width: Int?,
	val height: Int?,
	val mimeType: String?,
	val breedId: String?,
	val breedName: String?,
	val categoryId: Int?,
	val categoryName: String?
)
