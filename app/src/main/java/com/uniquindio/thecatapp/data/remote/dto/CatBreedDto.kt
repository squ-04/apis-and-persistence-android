package com.uniquindio.thecatapp.data.remote.dto

data class CatBreedDto(
    val id: String,
    val name: String,
    val origin: String?,
    val temperament: String?,
    val description: String?,
    @SerializedName("life_span")
    val lifeSpan: String?,
    @SerializedName("wikipedia_url")
    val wikipediaUrl: String?
)