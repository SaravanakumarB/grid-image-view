package com.example.grid_image_view.domain.scheduler

import io.reactivex.Scheduler
import org.koin.core.component.KoinComponent

interface IScheduler : KoinComponent {

  fun computation(): Scheduler

  fun io(): Scheduler

  fun ui(): Scheduler

}
