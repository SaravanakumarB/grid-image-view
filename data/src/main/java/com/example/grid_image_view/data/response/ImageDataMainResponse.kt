package com.example.grid_image_view.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
class ImageDataMainResponse(
    @SerializedName("data")
    var data: ArrayList<ImageDataResponse> = arrayListOf(),
) : Parcelable