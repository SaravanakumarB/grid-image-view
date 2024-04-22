package com.example.grid_image_view.koin.activity

import com.example.grid_image_view.koin.di.commonModuleKoin
import com.example.grid_image_view.koin.di.dataModuleKoin
import com.example.grid_image_view.koin.di.domainModuleKoin
import com.example.grid_image_view.koin.di.networkModuleKoin
import com.example.grid_image_view.page.MainActivity
import com.example.grid_image_view.page.MainActivityApiCall
import com.example.grid_image_view.page.MainActivityPresenter
import com.example.grid_image_view.page.MainActivityViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/** Root activity module. */
val activityModuleKoin = module {
  includes(commonModuleKoin, dataModuleKoin, domainModuleKoin, networkModuleKoin)

  scope<MainActivity>() {
    viewModel { MainActivityViewModel() }
    scoped { params ->
      MainActivityPresenter(
        androidApplication(), params.get(), get(), get()
      )
    }
  }

  factory { params -> MainActivityApiCall(params.get(), get(), get(), get()) }

  single { MainActivityViewModel() }
}

