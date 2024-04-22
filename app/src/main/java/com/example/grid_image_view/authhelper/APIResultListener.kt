package com.example.grid_image_view.authhelper

import com.example.grid_image_view.authhelper.ErrorEnum
import com.example.grid_image_view.domain.usecase.BaseParams
import io.reactivex.disposables.Disposable

interface APIResultListener {

  fun onAddRxCall(d: Disposable)

  fun onAPIFailure(errorEnum: ErrorEnum, error: Throwable?)

  fun onAPISuccess(resultType: String, result: Any?)

  fun onClearAllCalls()

  fun onServerFailure(failureData: HashMap<String, Any>)

  fun startLoadingView()

  fun stopLoadingView()

  fun onRefreshTokenExpired()

  fun onAccessTokenResultSuccess(apiType: String, params: BaseParams?)
}
