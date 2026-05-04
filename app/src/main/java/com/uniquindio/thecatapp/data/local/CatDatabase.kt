package com.uniquindio.thecatapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.uniquindio.thecatapp.data.local.dao.BreedDao
import com.uniquindio.thecatapp.data.local.dao.CatDetailDao
import com.uniquindio.thecatapp.data.local.dao.CatImageDao
import com.uniquindio.thecatapp.data.local.dao.CategoryDao
import com.uniquindio.thecatapp.data.local.entity.BreedEntity
import com.uniquindio.thecatapp.data.local.entity.CatDetailEntity
import com.uniquindio.thecatapp.data.local.entity.CatImageEntity
import com.uniquindio.thecatapp.data.local.entity.CategoryEntity

@Database(
	entities = [CatImageEntity::class, CatDetailEntity::class, BreedEntity::class, CategoryEntity::class],
	version = 2,
	exportSchema = false
)
abstract class CatDatabase : RoomDatabase() {

	abstract fun catImageDao(): CatImageDao

	abstract fun catDetailDao(): CatDetailDao

	abstract fun breedDao(): BreedDao

	abstract fun categoryDao(): CategoryDao
}
