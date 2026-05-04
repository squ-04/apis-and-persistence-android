package com.uniquindio.thecatapp.di

import android.content.Context
import androidx.room.Room
import com.uniquindio.thecatapp.data.local.CatDatabase
import com.uniquindio.thecatapp.data.local.dao.BreedDao
import com.uniquindio.thecatapp.data.local.dao.CatDetailDao
import com.uniquindio.thecatapp.data.local.dao.CatImageDao
import com.uniquindio.thecatapp.data.local.dao.CategoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

	@Provides
	@Singleton
	fun provideDatabase(
		@ApplicationContext context: Context
	): CatDatabase {
		return Room.databaseBuilder(
			context,
			CatDatabase::class.java,
			"cats.db"
		).fallbackToDestructiveMigration(dropAllTables = true).build()
	}

	@Provides
	fun provideCatImageDao(database: CatDatabase): CatImageDao = database.catImageDao()

	@Provides
	fun provideCatDetailDao(database: CatDatabase): CatDetailDao = database.catDetailDao()

	@Provides
	fun provideBreedDao(database: CatDatabase): BreedDao = database.breedDao()

	@Provides
	fun provideCategoryDao(database: CatDatabase): CategoryDao = database.categoryDao()
}
