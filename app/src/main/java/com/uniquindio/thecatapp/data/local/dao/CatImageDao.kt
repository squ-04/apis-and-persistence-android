package com.uniquindio.thecatapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uniquindio.thecatapp.data.local.entity.CatImageEntity

@Dao
interface CatImageDao {

	@Query("SELECT * FROM cat_images WHERE queryKey = :queryKey AND page = :page ORDER BY itemOrder ASC")
	suspend fun getPage(queryKey: String, page: Int): List<CatImageEntity>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(items: List<CatImageEntity>)

	@Query("DELETE FROM cat_images WHERE queryKey = :queryKey AND page = :page")
	suspend fun deletePage(queryKey: String, page: Int)
}
