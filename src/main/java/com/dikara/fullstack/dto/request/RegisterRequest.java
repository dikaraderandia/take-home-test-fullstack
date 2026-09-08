package com.dikara.fullstack.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {



    private String username;

    @Size(min = 6)
    private String password;

    @NotBlank
    private String passwordConfirmation;

}