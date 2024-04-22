package com.example.grid_image_view.authhelper

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.VisibleForTesting

/** Utility to get the current connection status of the device. */
class NetworkConnectionUtil (private val context: Context) {
  /** Enum to distinguish different connection statuses for the device. */
  enum class ConnectionStatus {
    /** Connected to WIFI or Ethernet. */
    LOCAL,
    /** Connected to Mobile or WiMax. */
    CELLULAR,
    /** Not connected to a network. */
    NONE
  }

  private var testConnectionStatus: ConnectionStatus? = null

  fun getCurrentConnectionStatus(): ConnectionStatus {
    testConnectionStatus?.let {
      return it
    }
    return getStatus()
  }

  @Suppress("DEPRECATION")
  fun getStatus(): ConnectionStatus {
    var result = ConnectionStatus.NONE
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      connectivityManager?.run {
        connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.run {
          result = when {
            hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> ConnectionStatus.LOCAL
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> ConnectionStatus.CELLULAR
            hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> ConnectionStatus.LOCAL
            else -> ConnectionStatus.NONE
          }
        }
      }
    } else {
      connectivityManager?.run {
        connectivityManager.activeNetworkInfo?.run {
          val isConnected = this.isConnected
          val isLocal =
            this.type == ConnectivityManager.TYPE_WIFI || this.type == ConnectivityManager.TYPE_ETHERNET
          val isCellular =
            this.type == ConnectivityManager.TYPE_MOBILE || this.type == ConnectivityManager.TYPE_WIMAX
          return when {
            isConnected && isLocal -> ConnectionStatus.LOCAL
            isConnected && isCellular -> ConnectionStatus.CELLULAR
            else -> ConnectionStatus.NONE
          }
        }
      }
    }
    return result
  }

  @VisibleForTesting(otherwise = VisibleForTesting.NONE)
  fun setCurrentConnectionStatus(status: ConnectionStatus) {
    testConnectionStatus = status
  }
}
