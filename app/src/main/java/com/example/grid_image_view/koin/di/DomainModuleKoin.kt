package com.example.grid_image_view.koin.di

import com.example.grid_image_view.domain.usecase.GetImageDataRequestUseCase

import org.koin.dsl.module

val domainModuleKoin = module {
  includes(dataModuleKoin)

  /** Get Image flow **/
  factory { GetImageDataRequestUseCase(get()) }
}
