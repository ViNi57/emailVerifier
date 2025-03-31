package com.lovelace.emailverifier.function;

import com.lovelace.emailverifier.service.MailVerifierService;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class MailVerifierFunction {
    private final MailVerifierService mailVerifierService;

    public MailVerifierFunction(MailVerifierService mailVerifierService) {
        this.mailVerifierService = mailVerifierService;
    }

    @Bean
    public Supplier<String> test() {
        return () -> "Hello World";
    }

    @Bean
    public Function<String, String> verifyEmail() {
        return mailVerifierService::verifyEmail;
    }
}