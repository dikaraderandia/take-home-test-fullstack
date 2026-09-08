package com.dikara.fullstack.service.impl;

import com.dikara.fullstack.dto.request.LoginRequest;
import com.dikara.fullstack.dto.request.RegisterRequest;
import com.dikara.fullstack.dto.response.LoginResponse;
import com.dikara.fullstack.entity.User;
import com.dikara.fullstack.exception.DuplicateResourceException;
import com.dikara.fullstack.repository.UserRepository;
import com.dikara.fullstack.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailServiceImpl userDetailsService;

    @Mock
    private JwtService jwtService;

    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(
                userRepository,
                passwordEncoder,
                authenticationManager,
                userDetailsService,
                jwtService
        );
    }

    // ---------- REGISTER ----------

    @Test
    void register_withNewUsername_shouldSaveUser() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("secret123");

        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-password");

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User saved = userCaptor.getValue();

        assertEquals("john", saved.getUsername());
        assertEquals("encoded-password", saved.getPassword());
        assertNotNull(saved.getCreatedAt());
    }

    @Test
    void register_withExistingUsername_shouldThrowDuplicateResourceException() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("secret123");

        User existing = User.builder()
                .id(UUID.randomUUID())
                .username("john")
                .password("encoded")
                .build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(existing));

        assertThrows(DuplicateResourceException.class, () -> authService.register(request));

        verify(userRepository, never()).save(any());
    }

    // ---------- LOGIN ----------

    @Test
    void login_withValidCredentials_shouldReturnTokens() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("secret123");

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("john")
                        .password("encoded")
                        .authorities("ROLE_USER")
                        .build();

        when(userDetailsService.loadUserByUsername("john")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(userDetails)).thenReturn("refresh-token");

        LoginResponse response = authService.login(request);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("john", "secret123")
        );

        assertEquals("access-token", response.getToken());
        assertEquals("refresh-token", response.getRefreshToken());
        assertEquals("john", response.getUsername());
    }

    @Test
    void login_withInvalidCredentials_shouldPropagateBadCredentialsException() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("wrong-password");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(
                BadCredentialsException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).generateToken(any());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
    }

    @Test
    void login_shouldDelegateCorrectOrderOfOperations() {
        LoginRequest request = new LoginRequest();
        request.setUsername("jane");
        request.setPassword("secret123");

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername("jane")
                        .password("encoded")
                        .authorities("ROLE_USER")
                        .build();

        when(userDetailsService.loadUserByUsername("jane")).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("at");
        when(jwtService.generateRefreshToken(userDetails)).thenReturn("rt");

        authService.login(request);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("jane", "secret123")
        );
        verify(userDetailsService).loadUserByUsername("jane");
        verify(jwtService).generateToken(userDetails);
        verify(jwtService).generateRefreshToken(userDetails);
    }
}
