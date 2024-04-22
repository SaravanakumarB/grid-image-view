package com.example.grid_image_view.di

import com.example.grid_image_view.BuildConfig
import com.example.grid_image_view.authhelper.KenkoNetworkAuthInterceptor
import com.example.grid_image_view.data.IService
import com.example.grid_image_view.data.utils.RequestInterceptor
import com.example.grid_image_view.errorhandling.IAPIExceptionMapper
import com.example.grid_image_view.errorhandling.APIExceptionMapper
import com.google.gson.Gson
import com.example.grid_image_view.data.utils.GsonUtil
import com.example.grid_image_view.errorhandling.RxErrorHandlingCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class NetworkModule {

  fun provideRequestInterceptor(
  ): RequestInterceptor {
    return RequestInterceptor()
  }

  fun getInterceptorList(
    requestInterceptor: RequestInterceptor,
    kenkoNetworkAuthInterceptor: KenkoNetworkAuthInterceptor
  ): MutableList<@JvmSuppressWildcards Interceptor> {
    val interceptorList = ArrayList<Interceptor>()
    interceptorList.add(requestInterceptor)
    interceptorList.add(kenkoNetworkAuthInterceptor)
    val loggingInterceptor = HttpLoggingInterceptor()
    if (BuildConfig.DEBUG) {
      loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    } else {
      loggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
    }
    interceptorList.add(loggingInterceptor)
    return interceptorList.toMutableList()
  }

  fun provideAPIExceptionMapper(): IAPIExceptionMapper {
    return APIExceptionMapper()
  }

  fun provideOkHttpClient(interceptorList: MutableList<Interceptor>): OkHttpClient {
    var builder = OkHttpClient.Builder()
    builder.protocols(listOf(Protocol.HTTP_1_1))
    builder.interceptors().addAll(interceptorList)

    builder = builder.connectTimeout(CONNECTION_TIMEOUT_SEC.toLong(), TimeUnit.SECONDS)
    builder = builder.writeTimeout(WRITE_TIMEOUT_SEC.toLong(), TimeUnit.SECONDS)
    builder = builder.readTimeout(READ_TIMEOUT_SEC.toLong(), TimeUnit.SECONDS)
    return builder.build()
  }

  fun provideGson(): Gson {
    return GsonUtil.getInstance().gson
  }

  fun providesService(
    okHttpClient: OkHttpClient,
    gson: Gson,
    exceptionMapper: IAPIExceptionMapper
  ): IService {
    val retrofit = Retrofit.Builder()
      .baseUrl(BuildConfig.BASE_SERVER_URL) // Need to be changed once it is working
      .client(okHttpClient)
      .addConverterFactory(ScalarsConverterFactory.create())
      .addConverterFactory(GsonConverterFactory.create(gson))
      .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create(exceptionMapper))
      .build()

    return retrofit.create(IService::class.java)
  }

  companion object {
    const val CONNECTION_TIMEOUT_SEC = 90
    const val WRITE_TIMEOUT_SEC = 120
    const val READ_TIMEOUT_SEC = 90
  }

}
