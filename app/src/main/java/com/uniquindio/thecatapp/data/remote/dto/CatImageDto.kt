package com.uniquindio.thecatapp.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CatImageDto(
    val id: String?,
    val url: String?,
    val width: Int?,
    val height: Int?,
    @SerializedName("mime_type")
    val mimeType: String?,
    val breeds: List<CatBreedDto>?,
    val categories: List<CatCategoryDto>?
)