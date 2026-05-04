package com.uniquindio.thecatapp.data.remote.dto

data class CatImageDto(
    val id: String,
    val url: String,
    val width: Int?,
    val height: Int?,
    @SerializedName("mime_type")
    val mimeType: String?,
    val breeds: List<CatBreedDto> = emptyList(),
    val categories: List<CatCategoryDto> = emptyList()
)