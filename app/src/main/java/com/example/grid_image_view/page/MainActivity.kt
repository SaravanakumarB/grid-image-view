package com.example.grid_image_view.page

import android.os.Bundle
import com.example.grid_image_view.activity.InjectableAppCompatActivity
import com.example.grid_image_view.authhelper.APIResultListener
import com.example.grid_image_view.authhelper.ErrorEnum
import com.example.grid_image_view.domain.usecase.BaseParams
import io.reactivex.disposables.Disposable
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : InjectableAppCompatActivity(), APIResultListener {

    private val mainActivityPresenter: MainActivityPresenter by inject { parametersOf(this) }

    companion object {
        internal const val TAG = "MainActivity.tag"
    }

    private var isConnected = true
    private fun setConnectivityListener() {
        setNetworkChangeListener(object : INetworkChangeListener {
            override fun networkConnected() {
                if (!isConnected) {
                    isConnected = true
                }
            }

            override fun networkDisconnected() {
                isConnected = false
                showNoInternetConnectionDialog(true)
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivityPresenter.handleOnCreate()
    }

    override fun onPause() {
        super.onPause()
        setNetworkChangeListener(null)
    }

    override fun onResume() {
        super.onResume()
        setConnectivityListener()
        mainActivityPresenter.handleOnResume()
    }

    override fun onDestroy() {
        mainActivityPresenter.unsubscribe()
        super.onDestroy()
    }

    override fun onAddRxCall(d: Disposable) {

    }

    override fun onAPIFailure(errorEnum: ErrorEnum, error: Throwable?) {
        mainActivityPresenter.handleAPIFailure(errorEnum, error)
    }

    override fun onAPISuccess(resultType: String, result: Any?) {
        mainActivityPresenter.handleAPISuccess(resultType, result)
    }

    override fun onClearAllCalls() {

    }

    override fun onServerFailure(failureData: HashMap<String, Any>) {

    }

    override fun startLoadingView() {
        mainActivityPresenter.showLoading()
    }

    override fun stopLoadingView() {
        mainActivityPresenter.hideLoading()
    }

    override fun onRefreshTokenExpired() {
        TODO("Not yet implemented")
    }

    override fun onAccessTokenResultSuccess(apiType: String, params: BaseParams?) {
        TODO("Not yet implemented")
    }
}