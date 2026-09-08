package com.dikara.fullstack;

import com.dikara.fullstack.dto.request.RegisterRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterRequestValidationTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void registerRequest_withValidFields_shouldPassValidation() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("secret123");
        request.setPasswordConfirmation("secret123");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void registerRequest_withBlankPasswordConfirmation_shouldFailValidation() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("secret123");
        request.setPasswordConfirmation(" ");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("passwordConfirmation")));
    }

    @Test
    void registerRequest_withNullPasswordConfirmation_shouldFailValidation() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("secret123");
        request.setPasswordConfirmation(null);

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("passwordConfirmation")));
    }

    @Test
    void registerRequest_withPasswordShorterThanMin_shouldFailValidation() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("12345");
        request.setPasswordConfirmation("12345");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertTrue(violations.stream()
                .anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void registerRequest_withPasswordExactlyMinLength_shouldPassValidation() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword("123456");
        request.setPasswordConfirmation("123456");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        assertTrue(violations.isEmpty());
    }

    @Test
    void registerRequest_withNullPassword_shouldPassValidation() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("john");
        request.setPassword(null);
        request.setPasswordConfirmation("secret123");

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        // @Size(min=6) tidak menolak null; hanya menolak string pendek.
        assertTrue(violations.stream()
                .noneMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    void registerRequest_withMissingFields_shouldReportAllViolations() {
        RegisterRequest request = new RegisterRequest();

        Set<ConstraintViolation<RegisterRequest>> violations =
                validator.validate(request);

        Set<String> fields = violations.stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());

        // password (null) lolos @Size(min=6); hanya passwordConfirmation yang @NotBlank.
        assertTrue(fields.contains("passwordConfirmation"));
    }
}
