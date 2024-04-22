package com.example.grid_image_view.data.repositories

import com.example.grid_image_view.data.IService
import com.example.grid_image_view.data.mappers.ImageMapper
import com.example.grid_image_view.data.response.ImageDataMainResponse
import com.example.grid_image_view.domain.datasources.ImageDataSources
import com.example.grid_image_view.domain.response.ImageDataMainModel
import io.reactivex.Single
import org.mapstruct.factory.Mappers

class ImageDataRepository (private var service: IService) : ImageDataSources {

    private val mapper = Mappers.getMapper(ImageMapper::class.java)

    override fun getImageDataRequest(total: Int): Single<ImageDataMainModel> {
        return service.getImageData(total).map {
            val imageDataMainResponse = ImageDataMainResponse();
            imageDataMainResponse.data = it
            mapper.convertImageDataToDomainModel(imageDataMainResponse)
        }
    }
}