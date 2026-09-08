package com.dikara.fullstack.common;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private Integer statusCode;

    private String message;

    private T data;
}