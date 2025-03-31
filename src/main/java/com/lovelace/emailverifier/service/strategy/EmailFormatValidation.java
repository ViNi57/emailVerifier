package com.lovelace.emailverifier.service.strategy;

import com.lovelace.emailverifier.service.domain.EmailValidationResult;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.regex.Pattern;

@Component
public class EmailFormatValidation implements EmailValidationStrategy {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    @Override
    public EmailValidationResult validate(String email) {
        if (Objects.isNull(email) || email.trim().isEmpty()) {
            return EmailValidationResult.invalid("Email cannot be empty");
        }

        return EMAIL_PATTERN.matcher(email).matches()
                ? EmailValidationResult.valid()
                : EmailValidationResult.invalid("Invalid email format");
    }
}