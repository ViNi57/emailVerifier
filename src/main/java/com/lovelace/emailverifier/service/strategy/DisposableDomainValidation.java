package com.lovelace.emailverifier.service.strategy;

import com.lovelace.emailverifier.service.domain.EmailValidationResult;
import com.lovelace.emailverifier.util.EmailUtils;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DisposableDomainValidation implements EmailValidationStrategy {
    private static final Set<String> DISPOSABLE_DOMAINS = Set.of("tempmail.com", "10minutemail.com", "mailinator.com");

    @Override
    public EmailValidationResult validate(String email) {
        String domain = EmailUtils.extractDomain(email);
        return !DISPOSABLE_DOMAINS.contains(domain)
                ? EmailValidationResult.valid()
                : EmailValidationResult.invalid("Disposable email domain not allowed");
    }
}