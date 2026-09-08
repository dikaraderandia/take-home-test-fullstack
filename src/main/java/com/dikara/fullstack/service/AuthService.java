package com.dikara.fullstack.service;

import com.dikara.axa.dto.request.LoginRequest;
import com.dikara.axa.dto.request.RegisterRequest;
import com.dikara.axa.dto.response.LoginResponse;

public interface AuthService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
