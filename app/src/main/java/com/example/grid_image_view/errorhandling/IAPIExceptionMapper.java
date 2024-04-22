package com.example.grid_image_view.errorhandling;

import java.io.IOException;

import retrofit2.HttpException;

public interface IAPIExceptionMapper {
    Throwable decodeHttpException(String apiUrl, String request, HttpException exception);

    Throwable decodeUnexpectedException(String apiUrl, String request, Throwable throwable);

    Throwable decodeIOException(String apiUrl, String request, IOException ioException);
}
