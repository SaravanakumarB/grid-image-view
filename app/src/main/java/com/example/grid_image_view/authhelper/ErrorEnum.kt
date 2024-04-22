package com.example.grid_image_view.authhelper

import android.content.Context
import androidx.annotation.StringRes
import com.example.grid_image_view.R

/** Use this class only for api calls. */
enum class ErrorEnum(@StringRes private var error: Int?) {
  UNEXPECTED_ERROR(error = R.string.unexpected_error),
  UNEXPECTED_NOVA_ERROR(error = R.string.unexpected_nova_error),
  UNEXPECTED_PPMC_ERROR(error = R.string.unexpected_error),
  //update dob error - used for showing dob api error separately from other dashboard api call error
  //with this help, we can handle update dob bottom sheet better.
  UPDATE_DOB_ERROR(error = R.string.unexpected_error);

  fun getErrorMessageFromStringRes(context: Context): String {
    return error?.let(context::getString) ?: context.getString(R.string.unexpected_error)
  }
}
