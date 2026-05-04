package com.uniquindio.thecatapp.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

interface ConnectivityObserver {
	val isOnline: Flow<Boolean>
}

@Singleton
class DefaultConnectivityObserver @Inject constructor(
	private val context: Context
) : ConnectivityObserver {

	override val isOnline: Flow<Boolean> = callbackFlow {
		val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

		fun isCurrentlyOnline(): Boolean {
			val network = connectivityManager.activeNetwork ?: return false
			val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
			return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
		}

		trySend(isCurrentlyOnline())

		val callback = object : ConnectivityManager.NetworkCallback() {
			override fun onAvailable(network: Network) {
				trySend(true)
			}

			override fun onLost(network: Network) {
				trySend(isCurrentlyOnline())
			}
		}

		val request = NetworkRequest.Builder().build()
		connectivityManager.registerNetworkCallback(request, callback)

		awaitClose {
			runCatching { connectivityManager.unregisterNetworkCallback(callback) }
		}
	}
}
