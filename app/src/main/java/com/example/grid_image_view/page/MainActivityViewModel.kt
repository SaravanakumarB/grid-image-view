package com.example.grid_image_view.page

import androidx.lifecycle.MutableLiveData
import com.redkenko.health.viewmodel.ObservableViewModel

class MainActivityViewModel () : ObservableViewModel() {
    val showLoadingView = MutableLiveData<Boolean>(false)
    val showLoadMoreView = MutableLiveData<Boolean>(false)
}