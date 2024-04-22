package com.example.grid_image_view.domain.response

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Error(
  var code: Int = 0,
  var message: String = "",
  var respId: String = ""
) : Parcelable
