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

fun CatImageDto.toEntity(queryKey: String, page: Int, itemOrder: Int, updatedAt: Long): CatImageEntity {
    return CatImageEntity(
        queryKey = queryKey,
        page = page,
        itemOrder = itemOrder,
        catId = id,
        url = url,
        breedId = breeds.firstOrNull()?.id,
        categoryId = categories.firstOrNull()?.id,
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

fun CatBreedDto.toDomain(): CatBreed {
    return CatBreed(
        id = id,
        name = name
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

fun CatCategoryDto.toDomain(): CatCategory {
    return CatCategory(
        id = id,
        name = name
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

