package com.example.grid_image_view.errorhandling

import java.io.IOException

class RetrofitException constructor(
  var kind: Kind = Kind.UNEXPECTED,
  var code: Int = CODE_1100,
  var errorMessage: String = "",
  var respId: String = "",
  var apiUrl: String = "",
  var requestBody: String = ""
) : RuntimeException() {

  /**
   * Identifies the event kind which triggered a [RetrofitException].
   */
  enum class Kind {
    /**
     * An [IOException] occurred while communicating to the server.
     */
    NETWORK,
    /**
     * A non-200 HTTP status code was received from the server.
     */
    HTTP,
    /**
     * An internal error occurred while attempting to execute a request. It is best practice to
     * re-throw this exception so your application crashes.
     */
    UNEXPECTED
  }

  companion object {
    internal const val CODE_400 = 400
    internal const val CODE_401 = 401
    internal const val CODE_403 = 403
    internal const val CODE_404 = 404
    internal const val CODE_408 = 408
    internal const val CODE_500 = 500
    internal const val CODE_502 = 502
    internal const val CODE_503 = 503
    internal const val CODE_504 = 504
    internal const val CODE_1000 = 1000
    internal const val CODE_1100 = 1100
    internal const val CODE_1200 = 1200
  }
}
