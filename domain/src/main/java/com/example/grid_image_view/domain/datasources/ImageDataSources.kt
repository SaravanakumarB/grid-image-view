package com.example.grid_image_view.domain.datasources

import com.example.grid_image_view.domain.response.ImageDataMainModel
import io.reactivex.Single
import org.koin.core.component.KoinComponent

interface ImageDataSources : KoinComponent {
    fun getImageDataRequest(total: Int): Single<ImageDataMainModel>
}