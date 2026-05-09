package com.uniquindio.thecatapp.core.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

interface ConnectivityObserver {
    val isOnline: Flow<Boolean>
}

@Singleton
class DefaultConnectivityObserver @Inject constructor(
    @ApplicationContext private val context: Context
) : ConnectivityObserver {

    private val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    override val isOnline: Flow<Boolean> = callbackFlow {
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                check()
            }

            override fun onLost(network: Network) {
                // Al perder la red, informamos false inmediatamente
                trySend(false)
            }

            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                check()
            }

            private fun check() {
                val activeNetwork = cm.activeNetwork
                val capabilities = cm.getNetworkCapabilities(activeNetwork)
                val online = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                        capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
                trySend(online)
            }
        }

        // Estado inicial
        val initialCaps = cm.getNetworkCapabilities(cm.activeNetwork)
        trySend(initialCaps?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true &&
                initialCaps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true)

        cm.registerDefaultNetworkCallback(callback)

        awaitClose {
            cm.unregisterNetworkCallback(callback)
        }
    }.distinctUntilChanged()
}
