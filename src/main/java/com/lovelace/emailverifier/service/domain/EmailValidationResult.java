package com.lovelace.emailverifier.service.domain;

public record EmailValidationResult(boolean isValid, String message) {
    public static EmailValidationResult valid() {
        return new EmailValidationResult(true, "");
    }

    public static EmailValidationResult invalid(String message) {
        return new EmailValidationResult(false, message);
    }
}