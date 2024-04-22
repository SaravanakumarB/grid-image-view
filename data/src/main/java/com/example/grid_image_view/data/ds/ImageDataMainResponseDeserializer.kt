package com.example.grid_image_view.data.ds

import com.example.grid_image_view.data.response.ImageDataMainResponse
import com.example.grid_image_view.data.utils.GsonUtil
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

class ImageDataMainResponseDeserializer : JsonDeserializer<ImageDataMainResponse> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): ImageDataMainResponse {
        return GsonUtil.getInstance().kJsonDeserialization(ImageDataMainResponse(), json)
    }
}