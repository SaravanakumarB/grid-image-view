package com.example.grid_image_view.koin.fragment

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.grid_image_view.koin.di.commonModuleKoin
import com.example.grid_image_view.koin.di.dataModuleKoin
import com.example.grid_image_view.koin.di.domainModuleKoin
import com.example.grid_image_view.page.MainActivityViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/** Root activity module. */
val fragmentModuleKoin = module {
  includes(commonModuleKoin, dataModuleKoin, domainModuleKoin)

  /** Api Calls **/

  /** Elastic Search Api calls **/

  single { MainActivityViewModel() }
}