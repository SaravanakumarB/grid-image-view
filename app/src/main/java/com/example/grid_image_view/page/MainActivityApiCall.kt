package com.example.grid_image_view.page

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.grid_image_view.R
import com.example.grid_image_view.authhelper.APIResultListener
import com.example.grid_image_view.authhelper.BaseAPICall
import com.example.grid_image_view.authhelper.ErrorEnum
import com.example.grid_image_view.authhelper.NetworkConnectionUtil
import com.example.grid_image_view.authhelper.handleApiResponse
import com.example.grid_image_view.domain.response.ImageDataMainModel
import com.example.grid_image_view.domain.scheduler.IScheduler
import com.example.grid_image_view.domain.usecase.GetImageDataRequestUseCase
import io.reactivex.SingleObserver
import io.reactivex.disposables.Disposable

class MainActivityApiCall(
    private val activity: AppCompatActivity,
    private val scheduler: IScheduler,
    private val networkConnectionUtil: NetworkConnectionUtil,
    private val getImageDataRequestUseCase: GetImageDataRequestUseCase,
) : BaseAPICall() {

    private val apiResultListener = activity as APIResultListener

    fun getImageData(total: Int, isLoadMore: Boolean) {
        //Note : No need to handle api failure or error, it is background api.
        if (!checkInternet()) {
            return
        }
        if(!isLoadMore) {
            apiResultListener.startLoadingView()
        }
        getImageDataRequestUseCase.execute(GetImageDataRequestUseCase.Params(total))
            .subscribeOn(scheduler.io())
            .observeOn(scheduler.ui())
            .subscribe(object : SingleObserver<ImageDataMainModel> {

                override fun onSuccess(imageDataMainModel: ImageDataMainModel) {
                    if(!isLoadMore) {
                        apiResultListener.stopLoadingView()
                    }
                    handleApiResponse(
                        200,
                        doOnSuccess = {
                            apiResultListener.onAPISuccess(
                                GET_IMAGE_DATA,
                                imageDataMainModel
                            )
                        },
                        doOnError = {
                        },
                        doOnRefreshTokenExpire = {

                        },
                        doOnNegotiableError = {
                        }
                    )
                }

                override fun onSubscribe(d: Disposable) {
                    apiResultListener.onAddRxCall(d)
                }

                override fun onError(e: Throwable) {
                    if(!isLoadMore) {
                        apiResultListener.stopLoadingView()
                    }
                    apiResultListener.onAPIFailure(ErrorEnum.UNEXPECTED_ERROR, e)
                    Log.e(MainActivity.TAG, "onError: $e")
                }
            })
    }

    private fun checkInternet(): Boolean {
        if (networkConnectionUtil.getCurrentConnectionStatus() == NetworkConnectionUtil.ConnectionStatus.NONE) {
            Toast.makeText(
                activity,
                activity.getString(R.string.internet_connection_error),
                Toast.LENGTH_SHORT
            ).show()
            apiResultListener.stopLoadingView()
            return false
        }
        return true
    }

    companion object {
        internal const val GET_IMAGE_DATA = "GET_IMAGE_DATA"
    }
}