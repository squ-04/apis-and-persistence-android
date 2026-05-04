package com.uniquindio.thecatapp.di

import com.uniquindio.thecatapp.domain.repository.CatRepository
import com.uniquindio.thecatapp.data.repository.CatRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

	@Binds
	@Singleton
	abstract fun bindCatRepository(
		impl: CatRepositoryImpl
	): CatRepository
}
