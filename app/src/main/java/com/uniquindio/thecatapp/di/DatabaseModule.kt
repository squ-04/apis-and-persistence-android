package com.uniquindio.thecatapp.di

import com.uniquindio.thecatapp.data.local.CatMemoryStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

	@Provides
	@Singleton
	fun provideCatMemoryStore(): CatMemoryStore = CatMemoryStore()
}
