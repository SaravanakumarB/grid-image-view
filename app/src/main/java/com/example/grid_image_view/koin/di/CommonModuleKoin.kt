package com.example.grid_image_view.koin.di

import com.example.grid_image_view.authhelper.NetworkConnectionUtil
import com.example.grid_image_view.utils.CacheUtils
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val commonModuleKoin = module {
  includes(networkModuleKoin)

  single { NetworkConnectionUtil(androidContext()) }
  single { CacheUtils() }
}