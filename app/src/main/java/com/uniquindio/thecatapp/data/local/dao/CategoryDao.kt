package com.uniquindio.thecatapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uniquindio.thecatapp.data.local.entity.CategoryEntity

@Dao
interface CategoryDao {

	@Query("SELECT * FROM categories ORDER BY name ASC")
	suspend fun getAll(): List<CategoryEntity>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(items: List<CategoryEntity>)

	@Query("DELETE FROM categories")
	suspend fun deleteAll()
}
