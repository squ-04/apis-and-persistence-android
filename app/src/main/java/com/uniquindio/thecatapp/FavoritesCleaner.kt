package com.uniquindio.thecatapp

import com.uniquindio.thecatapp.domain.repository.CatRepository

/**
 * Utility to clear all favorites: attempts best-effort deletion on server for each favorite
 * and then clears the local favorites table.
 */
suspend fun clearAllFavorites(repository: CatRepository): Result<Unit> {
    return repository.clearAllFavorites()
}

