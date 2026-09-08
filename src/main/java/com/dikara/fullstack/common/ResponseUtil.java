package com.dikara.fullstack.common;

import org.springframework.http.HttpStatus;

public class ResponseUtil {

    private ResponseUtil() {
    }

    public static <T> ApiResponse<T> success(
            String message,
            T data
    ) {

        return ApiResponse.<T>builder()
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponse<Void> success(
            String message
    ) {

        return ApiResponse.<Void>builder()
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .build();
    }

    public static <T> ApiResponse<T> created(
            String message,
            T data
    ) {

        return ApiResponse.<T>builder()
                .statusCode(HttpStatus.CREATED.value())
                .message(message)
                .data(data)
                .build();
    }
}
