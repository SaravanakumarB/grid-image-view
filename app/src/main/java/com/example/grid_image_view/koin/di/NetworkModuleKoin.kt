package com.example.grid_image_view.koin.di

import com.example.grid_image_view.BuildConfig
import com.example.grid_image_view.authhelper.KenkoNetworkAuthInterceptor
import com.example.grid_image_view.data.IService
import com.example.grid_image_view.data.repositories.ImageDataRepository
import com.example.grid_image_view.data.schedulers.SchedulerProvider
import com.example.grid_image_view.data.utils.GsonUtil
import com.example.grid_image_view.data.utils.RequestInterceptor
import com.example.grid_image_view.domain.datasources.ImageDataSources
import com.example.grid_image_view.domain.scheduler.IScheduler
import com.example.grid_image_view.errorhandling.APIExceptionMapper
import com.example.grid_image_view.errorhandling.IAPIExceptionMapper
import com.example.grid_image_view.errorhandling.RxErrorHandlingCallAdapterFactory

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor

import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

const val CONNECTION_TIMEOUT_SEC = 90
const val WRITE_TIMEOUT_SEC = 120
const val READ_TIMEOUT_SEC = 90

val networkModuleKoin = module {
  single { RequestInterceptor() }
  single { KenkoNetworkAuthInterceptor() }
  single<IAPIExceptionMapper> { APIExceptionMapper() }
  single { GsonUtil.getInstance().gson }

  single<IService> {
    val interceptorList = ArrayList<Interceptor>()
    interceptorList.add(get<RequestInterceptor>())
    interceptorList.add(get<KenkoNetworkAuthInterceptor>())
    val loggingInterceptor = HttpLoggingInterceptor()
    if (BuildConfig.DEBUG) {
      loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    } else {
      loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
    }
    interceptorList.add(loggingInterceptor)

    var builder = OkHttpClient.Builder()
    builder.protocols(listOf(Protocol.HTTP_1_1))
    builder.interceptors().addAll(interceptorList)

    builder = builder.connectTimeout(CONNECTION_TIMEOUT_SEC.toLong(), TimeUnit.SECONDS)
    builder = builder.writeTimeout(WRITE_TIMEOUT_SEC.toLong(), TimeUnit.SECONDS)
    builder = builder.readTimeout(READ_TIMEOUT_SEC.toLong(), TimeUnit.SECONDS)
    val client = builder.build()

    val retrofit = Retrofit.Builder()
      .baseUrl(BuildConfig.BASE_SERVER_URL) // Need to be changed once it is working
      .client(client)
      .addConverterFactory(ScalarsConverterFactory.create())
      .addConverterFactory(GsonConverterFactory.create(get()))
      .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create(get()))
      .build()
    retrofit.create(IService::class.java)
  }

  single<IScheduler> { SchedulerProvider.instance }
  single<ImageDataSources> { ImageDataRepository(get()) }
}

