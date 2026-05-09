package com.uniquindio.thecatapp.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
		val MIGRATION_3_4 = object : Migration(3, 4) {
			override fun migrate(database: SupportSQLiteDatabase) {
				// Destructive migration for favorites: drop existing (possibly mismatched) table and recreate
				database.execSQL("DROP TABLE IF EXISTS `favorites`")
				database.execSQL(
					"""
					CREATE TABLE `favorites` (
						`localId` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
						`id` INTEGER,
						`imageId` TEXT NOT NULL,
						`status` TEXT NOT NULL,
						`createdAt` INTEGER NOT NULL,
						`updatedAt` INTEGER
					)
					""".trimIndent()
				)
			}
		}

		return Room.databaseBuilder(
			context,
			CatDatabase::class.java,
			"cats.db"
		).addMigrations(MIGRATION_3_4).fallbackToDestructiveMigration(dropAllTables = true).build()
	}

	@Provides
	fun provideCatImageDao(database: CatDatabase): CatImageDao = database.catImageDao()

	@Provides
	fun provideCatDetailDao(database: CatDatabase): CatDetailDao = database.catDetailDao()

	@Provides
	fun provideBreedDao(database: CatDatabase): BreedDao = database.breedDao()

	@Provides
	fun provideCategoryDao(database: CatDatabase): CategoryDao = database.categoryDao()

	@Provides
	fun provideFavoriteDao(database: CatDatabase): com.uniquindio.thecatapp.data.local.dao.FavoriteDao = database.favoriteDao()
}
