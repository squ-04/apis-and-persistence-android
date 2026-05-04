package com.uniquindio.thecatapp.di

import android.content.Context
import com.uniquindio.thecatapp.BuildConfig
import com.uniquindio.thecatapp.core.network.ConnectivityObserver
import com.uniquindio.thecatapp.core.network.DefaultConnectivityObserver
import com.uniquindio.thecatapp.data.remote.CatApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

	@Provides
	@Singleton
	fun provideConnectivityObserver(
		@ApplicationContext context: Context
	): ConnectivityObserver = DefaultConnectivityObserver(context)

	@Provides
	@Singleton
	fun provideApiKeyInterceptor(): Interceptor {
		return Interceptor { chain ->
			val original = chain.request()
			val requestBuilder = original.newBuilder()

			if (BuildConfig.CAT_API_KEY.isNotBlank()) {
				requestBuilder.addHeader("x-api-key", BuildConfig.CAT_API_KEY)
			}

			chain.proceed(requestBuilder.build())
		}
	}

	@Provides
	@Singleton
	fun provideLoggingInterceptor(): HttpLoggingInterceptor {
		return HttpLoggingInterceptor().apply {
			level = HttpLoggingInterceptor.Level.BASIC
		}
	}

	@Provides
	@Singleton
	fun provideOkHttpClient(
		apiKeyInterceptor: Interceptor,
		loggingInterceptor: HttpLoggingInterceptor
	): OkHttpClient {
		return OkHttpClient.Builder()
			.addInterceptor(apiKeyInterceptor)
			.addInterceptor(loggingInterceptor)
			.build()
	}

	@Provides
	@Singleton
	fun provideRetrofit(client: OkHttpClient): Retrofit {
		return Retrofit.Builder()
			.baseUrl(BuildConfig.CAT_API_BASE_URL)
			.client(client)
			.addConverterFactory(GsonConverterFactory.create())
			.build()
	}

	@Provides
	@Singleton
	fun provideCatApiService(retrofit: Retrofit): CatApiService {
		return retrofit.create(CatApiService::class.java)
	}
}
