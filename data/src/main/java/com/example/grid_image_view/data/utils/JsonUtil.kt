package com.redkenko.health.data.utils

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

class JsonUtil {

  companion object {
    fun getDataJsonObject(jsonElement: JsonElement): JsonObject {
      val jsonObject = jsonElement.asJsonObject

      // intercept any other fixed object
      return jsonObject
    }

    fun getDataJsonArray(jsonElement: JsonElement): JsonArray {
      val jsonArray = jsonElement.asJsonArray

      // intercept any other fixed object
      return jsonArray
    }

    fun hasProperty(jsonElement: JsonObject, propertyName: String): Boolean {
      return jsonElement.has(propertyName) && !jsonElement.get(propertyName).isJsonNull
    }
  }

}
