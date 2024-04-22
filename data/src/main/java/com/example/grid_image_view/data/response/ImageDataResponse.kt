package com.example.grid_image_view.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class ImageDataResponse(
    @SerializedName("id")
    var id: String = "",

    @SerializedName("title")
    var title: String = "",

    @SerializedName("language")
    var language: String = "",

    @SerializedName("thumbnail")
    var thumbnail: ThumbnailResponse = ThumbnailResponse(),

    @SerializedName("mediaType")
    var mediaType: Int = 0,

    @SerializedName("coverageUrl")
    var coverageUrl: String = "",

    @SerializedName("publishedAt")
    var publishedAt: String = "",

    @SerializedName("publishedBy")
    var publishedBy: String = "",

    @SerializedName("backupDetails")
    var backupDetails: BackUpDetailResponse = BackUpDetailResponse(),
) : Parcelable

@Parcelize
class ThumbnailResponse(
    @SerializedName("id")
    var id: String = "",

    @SerializedName("version")
    var version: Int = 0,

    @SerializedName("domain")
    var domain: String = "",

    @SerializedName("basePath")
    var basePath: String = "",

    @SerializedName("key")
    var key: String = "",

    @SerializedName("qualities")
    var qualities: ArrayList<Int> = arrayListOf(),

    @SerializedName("aspectRatio")
    var aspectRatio: Int = 0,
): Parcelable

@Parcelize
class BackUpDetailResponse(
    @SerializedName("pdfLink")
    var pdfLink: String = "",

    @SerializedName("screenshotURL")
    var screenshotURL: String = "",
) : Parcelable