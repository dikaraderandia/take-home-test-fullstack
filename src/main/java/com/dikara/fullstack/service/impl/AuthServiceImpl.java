package com.dikara.fullstack.service.impl;

import com.dikara.fullstack.dto.request.LoginRequest;
import com.dikara.fullstack.dto.request.RegisterRequest;
import com.dikara.fullstack.dto.response.LoginResponse;
import com.dikara.fullstack.entity.User;
import com.dikara.fullstack.exception.DuplicateResourceException;
import com.dikara.fullstack.repository.UserRepository;
import com.dikara.fullstack.service.AuthService;
import com.dikara.fullstack.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final CustomUserDetailServiceImpl userDetailsService;

    private final JwtService jwtService;



    @Override
    public void register(RegisterRequest request) {

        log.info("Registering new user: {}", request.getUsername());

        if (userRepository.findByUsername(
                request.getUsername()).isPresent()) {

            log.warn("Registration failed - username already exists: {}", request.getUsername());
            throw new DuplicateResourceException(
                    "Username already exists"
            );
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(
                        passwordEncoder.encode(
                                request.getPassword()
                        )
                )
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(user);
        log.info("User registered successfully: {}", request.getUsername());
    }

    @Override
    public LoginResponse login(
            LoginRequest request
    ) {

        log.info("Login attempt for user: {}", request.getUsername());

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                                request.getUsername()
                        );

        String accessToken =
                jwtService.generateToken(
                        userDetails
                );

        String refreshToken =
                jwtService.generateRefreshToken(
                        userDetails
                );

        log.info("Login success for user: {}", request.getUsername());

        return LoginResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .username(userDetails.getUsername())
                .build();
    }
}
