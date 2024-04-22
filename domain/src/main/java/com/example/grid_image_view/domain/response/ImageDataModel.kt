package com.example.grid_image_view.domain.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageDataModel(
    var id: String = "",
    var title: String = "",
    var language: String = "",
    var thumbnail: ThumbnailData = ThumbnailData(),
    var mediaType: Int = 0,
    var coverageUrl: String = "",
    var publishedAt: String = "",
    var publishedBy: String = "",
    var backupDetails: BackUpDetailData = BackUpDetailData(),
) : Parcelable

@Parcelize
data class ThumbnailData(
    var id: String = "",
    var version: Int = 0,
    var domain: String = "",
    var basePath: String = "",
    var key: String = "",
    var qualities: ArrayList<Int> = arrayListOf(),
    var aspectRatio: Int = 0,
) : Parcelable

@Parcelize
data class BackUpDetailData(
    var pdfLink: String = "",
    var screenshotURL: String = "",
) : Parcelable