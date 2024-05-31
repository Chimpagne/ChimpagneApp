package com.monkeyteam.chimpagne.model.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService

// see https://developer.android.com/training/monitoring-device-state/connectivity-status-type
fun internetAccessListener(context: Context, onAvailable: () -> Unit, onLost: () -> Unit) {
  val networkRequest =
      NetworkRequest.Builder()
          .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
          .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
          .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
          .build()

  val networkCallback =
      object : ConnectivityManager.NetworkCallback() {
        // network is available for use
        override fun onAvailable(network: Network) {
          super.onAvailable(network)
          Log.d("Internet access", "connected")
          onAvailable()
        }

        // lost network connection
        override fun onLost(network: Network) {
          super.onLost(network)
          Log.d("Internet access", "not connected")
          onLost()
        }
      }

  val connectivityManager =
      getSystemService(context, ConnectivityManager::class.java) as ConnectivityManager
  connectivityManager.requestNetwork(networkRequest, networkCallback)
}

class NetworkNotAvailableException : Exception("No network available")
