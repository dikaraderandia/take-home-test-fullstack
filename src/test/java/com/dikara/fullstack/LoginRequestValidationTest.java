package com.dikara.fullstack;

import com.dikara.fullstack.dto.request.LoginRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void loginRequest_withValidFields_shouldPassValidation() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword("secret123");

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void loginRequest_withBlankUsername_shouldFailValidation() {
        LoginRequest request = new LoginRequest();
        request.setUsername(" ");
        request.setPassword("secret123");

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertTrue(violations.stream()
                .allMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void loginRequest_withNullUsername_shouldFailValidation() {
        LoginRequest request = new LoginRequest();
        request.setUsername(null);
        request.setPassword("secret123");

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertTrue(violations.stream()
                .allMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void loginRequest_withBlankPassword_shouldFailValidation() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword(" ");

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertTrue(violations.stream()
                .allMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void loginRequest_withNullPassword_shouldFailValidation() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");
        request.setPassword(null);

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertTrue(violations.stream()
                .allMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void loginRequest_withBothBlankFields_shouldFailValidation() {
        LoginRequest request = new LoginRequest();
        request.setUsername(null);
        request.setPassword(null);

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertEquals(2, violations.size());
    }

    @Test
    void loginRequest_withMissingUsername_shouldFailValidation() {
        LoginRequest request = new LoginRequest();
        request.setPassword("secret123");

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertTrue(violations.stream()
                .allMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    void loginRequest_withMissingPassword_shouldFailValidation() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john");

        Set<ConstraintViolation<LoginRequest>> violations =
                validator.validate(request);

        assertEquals(1, violations.size());
        assertTrue(violations.stream()
                .allMatch(v -> v.getPropertyPath().toString().equals("password")));
    }
}
