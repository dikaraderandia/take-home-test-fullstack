package com.dikara.fullstack.controller;

import com.dikara.fullstack.common.ApiResponse;
import com.dikara.fullstack.dto.request.LoginRequest;
import com.dikara.fullstack.dto.request.RegisterRequest;
import com.dikara.fullstack.dto.response.LoginResponse;
import com.dikara.fullstack.exception.DuplicateResourceException;
import com.dikara.fullstack.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(authService);
    }

    // ---------- REGISTER ----------

    @Test
    void register_shouldDelegateToServiceAndReturnSuccessResponse() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("secret123");
        request.setPasswordConfirmation("secret123");

        ResponseEntity<ApiResponse<Void>> response =
                authController.register(request);

        verify(authService).register(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(200, response.getBody().getStatusCode());
        assertEquals("Register success", response.getBody().getMessage());
    }

    @Test
    void register_shouldNotHaveDataBody() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");

        ResponseEntity<ApiResponse<Void>> response =
                authController.register(request);

        assertNull(response.getBody().getData());
    }

    @Test
    void register_whenServiceThrowsDuplicate_shouldPropagateException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");

        doThrow(new DuplicateResourceException("Username already exists"))
                .when(authService).register(any());

        assertThrows(
                DuplicateResourceException.class,
                () -> authController.register(request)
        );
    }

    // ---------- LOGIN ----------

    @Test
    void login_shouldReturnLoginResponse() {
        LoginResponse loginResponse = LoginResponse.builder()
                .token("access-token")
                .refreshToken("refresh-token")
                .username("john")
                .build();

        when(authService.login(any())).thenReturn(loginResponse);

        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("secret123");

        ResponseEntity<ApiResponse<LoginResponse>> response =
                authController.login(request);

        verify(authService).login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(200, response.getBody().getStatusCode());
        assertEquals("Login success", response.getBody().getMessage());
        assertEquals("access-token", response.getBody().getData().getToken());
        assertEquals("refresh-token", response.getBody().getData().getRefreshToken());
        assertEquals("john", response.getBody().getData().getUsername());
    }

    @Test
    void login_whenServiceReturnsAuthData_shouldReturnSameInstance() {
        LoginResponse loginResponse = LoginResponse.builder()
                .token("t")
                .refreshToken("r")
                .username("jane")
                .build();

        when(authService.login(any())).thenReturn(loginResponse);

        LoginRequest request = new LoginRequest();
        request.setUsername("jane");
        request.setPassword("secret123");

        ResponseEntity<ApiResponse<LoginResponse>> response =
                authController.login(request);

        assertEquals(loginResponse, response.getBody().getData());
    }
}
