package com.dikara.fullstack.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {


    String generateToken(UserDetails userDetails);



    Claims validate(String token);

    String extractUsername(
            String token
    );
}
