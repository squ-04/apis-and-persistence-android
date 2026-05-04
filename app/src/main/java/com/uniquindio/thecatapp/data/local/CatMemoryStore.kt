package com.uniquindio.thecatapp.data.local

import com.uniquindio.thecatapp.domain.model.Cat
import com.uniquindio.thecatapp.domain.model.CatBreed
import com.uniquindio.thecatapp.domain.model.CatCategory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CatMemoryStore @Inject constructor() {

    private val cats = listOf(
        Cat("1", "https://example.com/cat-1.jpg", breedId = "abys", categoryId = 1),
        Cat("2", "https://example.com/cat-2.jpg", breedId = "siam", categoryId = 2),
        Cat("3", "https://example.com/cat-3.jpg", breedId = "abys", categoryId = 2),
        Cat("4", "https://example.com/cat-4.jpg", breedId = "mau", categoryId = 1),
        Cat("5", "https://example.com/cat-5.jpg", breedId = "siam", categoryId = 3),
        Cat("6", "https://example.com/cat-6.jpg", breedId = "mau", categoryId = 3)
    )

    private val breeds = listOf(
        CatBreed("abys", "Abyssinian"),
        CatBreed("siam", "Siamese"),
        CatBreed("mau", "Egyptian Mau")
    )

    private val categories = listOf(
        CatCategory(1, "Funny"),
        CatCategory(2, "Sleepy"),
        CatCategory(3, "Cute")
    )

    fun getCats(page: Int, limit: Int, breedId: String?, categoryId: Int?): List<Cat> {
        val filtered = cats.filter { cat ->
            (breedId == null || cat.breedId == breedId) &&
                (categoryId == null || cat.categoryId == categoryId)
        }

        val fromIndex = page * limit
        return if (fromIndex >= filtered.size) {
            emptyList()
        } else {
            filtered.drop(fromIndex).take(limit)
        }
    }

    fun getBreeds(): List<CatBreed> = breeds

    fun getCategories(): List<CatCategory> = categories
}


