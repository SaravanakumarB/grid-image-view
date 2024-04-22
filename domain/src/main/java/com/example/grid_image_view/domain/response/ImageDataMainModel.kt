package com.example.grid_image_view.domain.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageDataMainModel(
    var data: ArrayList<ImageDataModel> = arrayListOf(),
) : Parcelable