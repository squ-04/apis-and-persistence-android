package com.uniquindio.thecatapp.data.remote.dto

data class CatBreedDto(
    val id: String,
    val name: String,
    val origin: String?,
    val temperament: String?,
    val description: String?,
    val lifeSpan: String?,
    val wikipediaUrl: String?
)