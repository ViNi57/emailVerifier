package com.lovelace.emailverifier.service.strategy;

import com.lovelace.emailverifier.service.domain.EmailValidationResult;
import org.springframework.stereotype.Component;

@Component
public interface EmailValidationStrategy {
    EmailValidationResult validate(String email);
}