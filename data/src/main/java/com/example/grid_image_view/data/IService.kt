package com.example.grid_image_view.data

import com.example.grid_image_view.data.response.ImageDataResponse
import io.reactivex.Single
import okhttp3.MultipartBody
import org.koin.core.component.KoinComponent
import retrofit2.Call
import retrofit2.http.*

// Main retrofit service calls
interface IService : KoinComponent {
  @GET("api/v2/content/misc/media-coverages")
  fun getImageData(@Query("limit") total: Int): Single<ArrayList<ImageDataResponse>>
}
