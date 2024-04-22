package com.example.grid_image_view.page

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.grid_image_view.R
import com.example.grid_image_view.authhelper.ErrorEnum
import com.example.grid_image_view.authhelper.NetworkConnectionUtil
import com.example.grid_image_view.databinding.ActivityMainBinding
import com.example.grid_image_view.domain.response.ImageDataMainModel
import com.example.grid_image_view.domain.response.ImageDataModel
import com.example.grid_image_view.errorhandling.RetrofitException
import com.example.grid_image_view.utils.CacheUtils
import com.example.grid_image_view.utils.Constants
import com.example.grid_image_view.utils.EndlessScrollListener
import com.redkenko.health.presenter.BasePresenter
import okhttp3.internal.cache.DiskLruCache
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf

class MainActivityPresenter(
    private val application: Application,
    private val activity: AppCompatActivity,
    private val networkConnectionUtil: NetworkConnectionUtil,
    private val cacheUtils: CacheUtils
) : BasePresenter() {

    private val mainActivityApiCall: MainActivityApiCall by inject { parametersOf(activity) }
    private val viewModel: MainActivityViewModel by activity.viewModel()

    private lateinit var binding: ActivityMainBinding
    private var imageDataListAdapter: ImageDataListAdapter ? = null
    private var endlessScrollListener: EndlessScrollListener? = null
    private var page = 1
    private var limit = 100
    private var imageList: ArrayList<ImageDataModel> = arrayListOf()

    fun handleOnCreate() {
        binding = DataBindingUtil.setContentView(activity, R.layout.activity_main)

        binding.let {
            it.lifecycleOwner = activity
            it.viewModel = viewModel
        }

        imageList.clear()
        cacheUtils.create(activity)
        createRecyclerView()
    }

    private fun createRecyclerView() {
        val layoutManager = GridLayoutManager(activity, 3)
        endlessScrollListener = object : EndlessScrollListener(layoutManager, 0) {
            override fun onLoadMore(pageIndex: Int, totalItemsCount: Int, view: RecyclerView?) {
                if (page * limit < 100) {
                    val visiblePosition = layoutManager.findFirstVisibleItemPosition()
                    if ((totalItemsCount - visiblePosition) <= 10) {
                        viewModel.showLoadMoreView.postValue(true)
                        mainActivityApiCall.getImageData(page * limit, true)
                    }
                }
            }

            override fun onScrollUp(
                page: Int,
                lastVisibleItemPosition: Int,
                totalItemsCount: Int,
                view: RecyclerView?
            ) {
                super.onScrollUp(page, lastVisibleItemPosition, totalItemsCount, view)
            }

            override fun onScrollToEnd(
                page: Int,
                lastVisibleItemPosition: Int,
                totalItemsCount: Int,
                view: RecyclerView?
            ) {
                super.onScrollToEnd(page, lastVisibleItemPosition, totalItemsCount, view)
            }
        }

        imageDataListAdapter = ImageDataListAdapter(activity, cacheUtils)

        binding.imageRecyclerView.apply {
            this.layoutManager = layoutManager
            adapter = imageDataListAdapter
            endlessScrollListener?.let { addOnScrollListener(it) }
        }
    }

    fun handleOnResume() {
        mainActivityApiCall.getImageData(page * limit, false)
    }
    override fun unsubscribe() {
        clearAllCalls()
    }

    override fun logout() {

    }

    fun handleAPIFailure(
        errorEnum: ErrorEnum,
        error: Throwable?
    ) {
        if (errorEnum == ErrorEnum.UNEXPECTED_NOVA_ERROR) {
            if (error != null) {

            } else {

            }
        } else if (error is RetrofitException) {
            if (error.code == Constants.REFRESH_TOKEN_EXPIRED) {

            } else {

            }
        } else {

        }
    }

    fun showLoading() {
        viewModel.showLoadingView.postValue(true)
    }

    fun hideLoading() {
        viewModel.showLoadingView.postValue(false)
    }

    fun handleAPISuccess(resultType: String, result: Any?) {
        if (resultType == MainActivityApiCall.GET_IMAGE_DATA) {
            if(page == 1) {
                imageList.clear()
                imageList.addAll((result as ImageDataMainModel).data)
                imageDataListAdapter?.setImageList(imageList)
            } else {
                viewModel.showLoadMoreView.postValue(false)
                val totalList = (result as ImageDataMainModel).data
                val previousSize = imageList.size
                val newList = totalList.subList(previousSize, totalList.size)
                imageList.addAll(newList)
                imageDataListAdapter?.setImageList(imageList)
                page += 1
            }
        }
    }
}