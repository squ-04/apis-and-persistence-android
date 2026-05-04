package com.uniquindio.thecatapp.di

import com.uniquindio.thecatapp.domain.repository.CatRepository
import com.uniquindio.thecatapp.data.repository.CatRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

	@Binds
	abstract fun bindCatRepository(
		impl: CatRepositoryImpl
	): CatRepository
}
