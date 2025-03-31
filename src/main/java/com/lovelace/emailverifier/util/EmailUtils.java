package com.lovelace.emailverifier.util;

public final class EmailUtils {
    private EmailUtils() {
    }

    public static String extractDomain(String email) {
        return email.substring(email.indexOf('@') + 1);
    }
}