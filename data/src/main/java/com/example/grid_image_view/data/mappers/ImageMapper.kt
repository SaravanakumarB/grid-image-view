package com.example.grid_image_view.data.mappers

import com.example.grid_image_view.data.response.ImageDataMainResponse
import com.example.grid_image_view.domain.response.ImageDataMainModel
import org.mapstruct.Mapper

@Mapper
interface ImageMapper {
    fun convertImageDataToDomainModel(imageDataMainResponse: ImageDataMainResponse): ImageDataMainModel

}