package com.uniquindio.thecatapp

import android.app.Application
import coil3.ImageLoader
import coil3.SingletonImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class TheCatAppApplication : Application() {

	override fun onCreate() {
		super.onCreate()

		SingletonImageLoader.setSafe { context ->
			ImageLoader.Builder(context)
				.components {
					add(OkHttpNetworkFetcherFactory())
				}
				.build()
		}
	}
}

