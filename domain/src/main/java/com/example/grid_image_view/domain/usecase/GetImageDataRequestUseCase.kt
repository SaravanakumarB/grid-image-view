package com.example.grid_image_view.domain.usecase

import com.example.grid_image_view.domain.datasources.ImageDataSources
import com.example.grid_image_view.domain.response.ImageDataMainModel
import io.reactivex.Single

class GetImageDataRequestUseCase(private val imageDataSources: ImageDataSources) :
    UseCase<GetImageDataRequestUseCase.Params?, Single<ImageDataMainModel>>() {
    override fun execute(params: Params?): Single<ImageDataMainModel> {
        return imageDataSources.getImageDataRequest(params!!.total)
    }

    class Params(val total: Int) : BaseParams
}