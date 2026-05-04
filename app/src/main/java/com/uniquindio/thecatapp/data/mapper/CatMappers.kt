package com.uniquindio.thecatapp.data.mapper

import com.uniquindio.thecatapp.data.local.entity.BreedEntity
import com.uniquindio.thecatapp.data.local.entity.CatImageEntity
import com.uniquindio.thecatapp.data.local.entity.CategoryEntity
import com.uniquindio.thecatapp.data.remote.dto.CatBreedDto
import com.uniquindio.thecatapp.data.remote.dto.CatCategoryDto
import com.uniquindio.thecatapp.data.remote.dto.CatImageDto
import com.uniquindio.thecatapp.domain.model.Cat
import com.uniquindio.thecatapp.domain.model.CatBreed
import com.uniquindio.thecatapp.domain.model.CatCategory

fun CatImageDto.toEntityOrNull(queryKey: String, page: Int, itemOrder: Int, updatedAt: Long): CatImageEntity? {
    val safeId = id?.takeIf { it.isNotBlank() } ?: return null
    val safeUrl = url?.takeIf { it.isNotBlank() } ?: return null

    return CatImageEntity(
        queryKey = queryKey,
        page = page,
        itemOrder = itemOrder,
        catId = safeId,
        url = safeUrl,
        breedId = breeds.orEmpty().firstOrNull()?.id,
        categoryId = categories.orEmpty().firstOrNull()?.id,
        updatedAt = updatedAt
    )
}

fun CatImageEntity.toDomain(): Cat {
    return Cat(
        id = catId,
        url = url,
        breedId = breedId,
        categoryId = categoryId
    )
}

fun CatBreedDto.toDomainOrNull(): CatBreed? {
    val safeId = id?.takeIf { it.isNotBlank() } ?: return null
    val safeName = name?.takeIf { it.isNotBlank() } ?: return null

    return CatBreed(
        id = safeId,
        name = safeName
    )
}

fun CatBreed.toEntity(updatedAt: Long): BreedEntity {
    return BreedEntity(
        id = id,
        name = name,
        updatedAt = updatedAt
    )
}

fun BreedEntity.toDomain(): CatBreed {
    return CatBreed(
        id = id,
        name = name
    )
}

fun CatCategoryDto.toDomainOrNull(): CatCategory? {
    val safeId = id ?: return null
    val safeName = name?.takeIf { it.isNotBlank() } ?: return null

    return CatCategory(
        id = safeId,
        name = safeName
    )
}

fun CatCategory.toEntity(updatedAt: Long): CategoryEntity {
    return CategoryEntity(
        id = id,
        name = name,
        updatedAt = updatedAt
    )
}

fun CategoryEntity.toDomain(): CatCategory {
    return CatCategory(
        id = id,
        name = name
    )
}

