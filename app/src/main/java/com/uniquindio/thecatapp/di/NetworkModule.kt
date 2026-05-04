package com.uniquindio.thecatapp.di

import android.content.Context
import com.uniquindio.thecatapp.core.network.ConnectivityObserver
import com.uniquindio.thecatapp.core.network.DefaultConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

	@Provides
	@Singleton
	fun provideConnectivityObserver(
		@ApplicationContext context: Context
	): ConnectivityObserver = DefaultConnectivityObserver(context)
}
