package com.uniquindio.thecatapp.data.remote

import com.uniquindio.thecatapp.data.remote.dto.CatBreedDto
import com.uniquindio.thecatapp.data.remote.dto.CatCategoryDto
import com.uniquindio.thecatapp.data.remote.dto.CatImageDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

// DTOs específicos para favourites (la API de TheCatAPI usa /favourites)
data class FavouriteRequestDto(
    val image_id: String,
    val sub_id: String? = null
)

data class FavouriteResponseDto(
    val message: String?,
    val id: Int?
)

interface CatApiService {
    @GET("images/search")
    suspend fun searchImages(
        @Query("size") size: String = "med",
        @Query("mime_types") mimeTypes: String = "jpg,png",
        @Query("format") format: String = "json",
        @Query("order") order: String = "ASC",
        @Query("page") page: Int,
        @Query("limit") limit: Int = 20,
        @Query("breed_ids") breedIds: String? = null,
        @Query("category_ids") categoryIds: String? = null,
        @Query("include_breeds") includeBreeds: Int = 1,
        @Query("include_categories") includeCategories: Int = 1
    ): List<CatImageDto>

    @GET("images/{imageId}")
    suspend fun getImageById(
        @Path("imageId") imageId: String
    ): CatImageDto

    @GET("breeds")
    suspend fun getBreeds(): List<CatBreedDto>

    @GET("categories")
    suspend fun getCategories(): List<CatCategoryDto>

    // Favoritos
    @POST("favourites")
    suspend fun addFavorite(@Body body: FavouriteRequestDto): FavouriteResponseDto

    @DELETE("favourites/{favourite_id}")
    suspend fun removeFavorite(@Path("favourite_id") favouriteId: Int)
}