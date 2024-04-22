package com.example.grid_image_view.di

import com.example.grid_image_view.data.IService
import com.example.grid_image_view.data.repositories.ImageDataRepository
import com.example.grid_image_view.data.schedulers.SchedulerProvider
import com.example.grid_image_view.domain.datasources.ImageDataSources
import com.example.grid_image_view.domain.scheduler.IScheduler

class DataModule {

  fun provideLoginDataSource(service: IService): ImageDataSources {
    return ImageDataRepository(service)
  }

  fun provideScheduler(): IScheduler {
    return SchedulerProvider.instance
  }
}
