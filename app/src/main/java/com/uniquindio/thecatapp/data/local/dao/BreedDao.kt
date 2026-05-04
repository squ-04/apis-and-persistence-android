package com.uniquindio.thecatapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.uniquindio.thecatapp.data.local.entity.BreedEntity

@Dao
interface BreedDao {

	@Query("SELECT * FROM breeds ORDER BY name ASC")
	suspend fun getAll(): List<BreedEntity>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun insertAll(items: List<BreedEntity>)

	@Query("DELETE FROM breeds")
	suspend fun deleteAll()
}
