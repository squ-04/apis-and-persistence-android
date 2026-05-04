package com.uniquindio.thecatapp.domain.model

data class Cat(
    val id: String,
    val url: String,
    val breedId: String? = null,
    val categoryId: Int? = null
)