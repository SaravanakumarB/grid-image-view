package com.example.grid_image_view.authhelper

import com.example.grid_image_view.BuildConfig
import com.example.grid_image_view.data.IService
import com.example.grid_image_view.data.mappers.ImageMapper
import com.example.grid_image_view.data.utils.GsonUtil
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import okio.IOException
import org.mapstruct.factory.Mappers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory


class KenkoNetworkAuthInterceptor (
) : Interceptor {

  private val lock = Any()

  private val mapper by lazy { Mappers.getMapper(ImageMapper::class.java) }

  private val iService: IService by lazy {
    createRetrofitInstance()
  }

  override fun intercept(chain: Interceptor.Chain): Response {
    synchronized(lock) {
      val oldRequest = chain.request()
      val response = chain.proceed(oldRequest)
      return response
    }
  }

  private fun buildNewRequest(
    request: Request,
    accessToken: String,
    requestBody: RequestBody?
  ): Request {
    // Have to separate logout because method was changing when recalling the api.
    return if (request.url.toUrl().toString().contains("logout", true)) {
      request.newBuilder()
        .removeHeader("Authorization")
        .addHeader("Authorization", "Bearer $accessToken").apply {
          method(request.method, requestBody)
        }
        .build()
    } else {
      request.newBuilder()
        .removeHeader("Authorization")
        .addHeader("Authorization", "Bearer $accessToken").apply {
          requestBody?.let { post(it) }
        }
        .build()
    }
  }

  private fun bodyToString(request: RequestBody): String? {
    return try {
      val buffer = Buffer()
      if (request != null) request.writeTo(buffer) else return ""
      buffer.readUtf8()
    } catch (e: IOException) {
      ""
    }
  }

  private fun createRetrofitInstance() = Retrofit.Builder()
    .baseUrl(BuildConfig.BASE_SERVER_URL) // Need to be changed once it is working
    .client(
      OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
          level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
          } else {
            HttpLoggingInterceptor.Level.NONE
          }
        })
        .build()
    )
    .addConverterFactory(ScalarsConverterFactory.create())
    .addConverterFactory(GsonConverterFactory.create(GsonUtil.getInstance().gson))
    .build()
    .create(IService::class.java)
}
