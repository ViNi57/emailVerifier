package com.lovelace.emailverifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@SpringBootApplication()
public class EmailVerifier {

    public static void main(String[] args) {
        SpringApplication.run(EmailVerifier.class, args);
    }

    @Bean
    public ConcurrentHashMap<String, List<String>> mxRecordsCache() {
        return new ConcurrentHashMap<>();
    }
}