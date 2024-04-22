package com.example.grid_image_view.koin.di

import com.example.grid_image_view.data.repositories.ImageDataRepository
import com.example.grid_image_view.data.schedulers.SchedulerProvider
import com.example.grid_image_view.domain.datasources.ImageDataSources
import com.example.grid_image_view.domain.scheduler.IScheduler
import org.koin.dsl.module

val dataModuleKoin = module {
  includes(networkModuleKoin)

  single<IScheduler> { SchedulerProvider.instance }
  single<ImageDataSources> { ImageDataRepository(get()) }
}