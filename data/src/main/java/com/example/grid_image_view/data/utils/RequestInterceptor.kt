package com.example.grid_image_view.data.utils

import com.example.grid_image_view.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.util.UUID

class RequestInterceptor(

) : Interceptor {

  companion object {
    const val BEARER: String = "Bearer "

    const val IS_HEADER_REQUIRED = "isHeaderRequired"
    const val IS_CASHLESS_HEADER_REQUIRED = "isCashlessHeaderRequired"

    const val HEADER_AUTHORIZATION = "Authorization"
    const val HEADER_CONTENT_TYPE = "Content-Type"
    const val HEADER_VALUE_CONTENT_TYPE = "application/json"
    const val HEADER_CACHE_CONTROL = "Cache-Control"
    const val HEADER_VALUE_NO_CACHE = "no-cache"
    const val HEADER_VALUE_PLATFORM = "x-platform"
    const val HEADER_VALUE_VERSION = "x-version"
    const val HEADER_VALUE_REQUEST_ID = "x-request-id"
  }

  @Throws(IOException::class)
  override fun intercept(chain: Interceptor.Chain): Response {
    synchronized(this) {
      var request = chain.request()

      val requestBuilder = request.newBuilder()

      requestBuilder.addHeader(HEADER_VALUE_PLATFORM, "ANDROID")
      requestBuilder.addHeader(HEADER_VALUE_VERSION, BuildConfig.VERSION_NAME)
      requestBuilder.addHeader(HEADER_VALUE_REQUEST_ID, "a-${UUID.randomUUID().toString()}")

      if (isHeaderRequired(request)) {
//        requestBuilder.addHeader(
//          HEADER_AUTHORIZATION,
//          BEARER + localPreferencesController.getAccessToken()
//        )
      }

      request = requestBuilder.build()
      return chain.proceed(request)
    }
  }

  private fun isHeaderRequired(request: Request): Boolean {
    return if (request.headers[IS_HEADER_REQUIRED] != null) {
      request.headers[IS_HEADER_REQUIRED].toString().toBoolean()
    } else {
      true
    }
  }

  private fun addHeaders(requestBuilder: Request.Builder) {
    requestBuilder
      .addHeader(HEADER_CONTENT_TYPE, HEADER_VALUE_CONTENT_TYPE)
      .addHeader(HEADER_CACHE_CONTROL, HEADER_VALUE_NO_CACHE).build()
  }
}