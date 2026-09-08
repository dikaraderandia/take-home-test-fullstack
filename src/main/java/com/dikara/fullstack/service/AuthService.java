package com.dikara.fullstack.service;

import com.dikara.fullstack.dto.request.LoginRequest;
import com.dikara.fullstack.dto.request.RegisterRequest;
import com.dikara.fullstack.dto.response.LoginResponse;

public interface AuthService {

    void register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}
