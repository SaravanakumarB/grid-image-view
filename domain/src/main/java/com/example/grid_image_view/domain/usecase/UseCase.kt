package com.example.grid_image_view.domain.usecase

import org.koin.core.component.KoinComponent

abstract class UseCase<INPUT_TYPE, OUTPUT_TYPE> : KoinComponent {
  abstract fun execute(params: INPUT_TYPE? = null): OUTPUT_TYPE
}
