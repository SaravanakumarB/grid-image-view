package com.example.grid_image_view.errorhandling

import com.example.grid_image_view.domain.response.Error
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.HttpException
import java.io.IOException

class APIExceptionMapper : IAPIExceptionMapper {

  override fun decodeHttpException(apiUrl: String, request: String, exception: HttpException): Throwable {

    val retrofitException = RetrofitException()
    retrofitException.kind = RetrofitException.Kind.HTTP
    try {
      val response = exception.response()
        val errorBody = response?.errorBody()!!.string()
        val jsonObject = JsonParser().parse(errorBody).asJsonObject
        val decodedError = decodeErrorModel(jsonObject)
        retrofitException.code = decodedError.code
        retrofitException.errorMessage = decodedError.message
        retrofitException.respId = decodedError.respId
    } catch (e: Exception) {
      retrofitException.code = RetrofitException.CODE_1200
      retrofitException.errorMessage = "Something went wrong!\nPlease try again later or visit Kenko Website to contact us."
      retrofitException.respId = ""
    }
    retrofitException.apiUrl = apiUrl
    retrofitException.requestBody = request
    return retrofitException
  }

  override fun decodeUnexpectedException(apiUrl: String, request: String, throwable: Throwable): Throwable {

    val retrofitException = RetrofitException()
    retrofitException.errorMessage = "Unexpected error occurred"
    retrofitException.code = RetrofitException.CODE_1100
    retrofitException.kind = RetrofitException.Kind.UNEXPECTED
    retrofitException.respId = ""
    retrofitException.apiUrl = apiUrl
    retrofitException.requestBody = request
    return retrofitException
  }

  override fun decodeIOException(apiUrl: String, request: String, ioException: IOException): Throwable {

    val retrofitException = RetrofitException()
    if("timeout".equals(ioException.message, true)) {
      retrofitException.code = RetrofitException.CODE_1200
      retrofitException.errorMessage = "Request timed out!\nPlease try again later or visit Kenko Website to contact us."
    } else {
      retrofitException.errorMessage = "Internet connection error"
      retrofitException.code = RetrofitException.CODE_1000
    }
    retrofitException.kind = RetrofitException.Kind.NETWORK
    retrofitException.respId = ""
    retrofitException.apiUrl = apiUrl
    retrofitException.requestBody = request
    return retrofitException
  }

  private fun decodeErrorModel(jsonObject: JsonObject): Error {
    val error = Error()
    if (jsonObject.has("code")) {
      val code = jsonObject.get("code").asInt
      error.code = code
    }
    if (jsonObject.has("errMsg")) {
      val message = jsonObject.get("errMsg").asString
      error.message = message
    }
    if (jsonObject.has("respId")) {
      val message = jsonObject.get("respId").asString
      error.respId = message
    }
    return error
  }

}