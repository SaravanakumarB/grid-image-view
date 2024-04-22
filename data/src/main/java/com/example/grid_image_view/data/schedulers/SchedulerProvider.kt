package com.example.grid_image_view.data.schedulers

import com.example.grid_image_view.domain.scheduler.IScheduler
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class SchedulerProvider : IScheduler {
  override fun computation(): Scheduler {
    return Schedulers.computation()
  }

  override fun io(): Scheduler {
    return Schedulers.io()
  }

  override fun ui(): Scheduler {
    return AndroidSchedulers.mainThread()
  }

  companion object {
    var instance: SchedulerProvider = SchedulerProvider()
  }
}
