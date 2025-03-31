package com.lovelace.emailverifier.service.smtp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class DefaultSmtpVerifier extends SmtpVerifier {
    private static final Logger logger = LoggerFactory.getLogger(DefaultSmtpVerifier.class);
    private static final List<String> HELO_DOMAINS = List.of("primeadagency.com");
    private static final List<String> VERIFICATION_EMAILS = List.of("abhiram.info@primeadagency.com");

    private final AtomicInteger heloDomainIndex = new AtomicInteger(0);
    private final AtomicInteger verificationEmailIndex = new AtomicInteger(0);

    @Override
    protected boolean performSmtpVerification(String email, Socket socket) throws IOException {


        try (var reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             var writer = new PrintWriter(socket.getOutputStream(), true)) {

            // Set timeout
            socket.setSoTimeout(5000);

            // 1. Read initial server greeting
            String initialResponse = readResponse(reader);
            logger.debug("Initial server response: {}", initialResponse);
            if (!isResponseValid(initialResponse, "220")) {
                logger.warn("Invalid initial response. Expected 220, got: {}", initialResponse);
                return false;
            }

            // 2. Send EHLO
            String heloDomain = getNextHeloDomain();
            writer.println("EHLO " + heloDomain);
            logger.debug("Sent EHLO with domain: {}", heloDomain);

            String ehloResponse = readResponse(reader);
            logger.debug("EHLO response: {}", ehloResponse);
            if (!isResponseValid(ehloResponse, "250")) {
                logger.warn("EHLO failed. Expected 250, got: {}", ehloResponse);
                return false;
            }

            // 3. Send MAIL FROM
            String verificationEmail = getNextVerificationEmail();
            writer.println("MAIL FROM: <" + verificationEmail + ">");
            logger.debug("Sent MAIL FROM: <{}>", verificationEmail);

            String mailFromResponse = readResponse(reader);
            logger.debug("MAIL FROM response: {}", mailFromResponse);
            if (!isResponseValid(mailFromResponse, "250")) {
                logger.warn("MAIL FROM failed. Expected 250, got: {}", mailFromResponse);
                return false;
            }

            // 4. Send RCPT TO
            writer.println("RCPT TO: <" + email + ">");
            String rcptResponse = readResponse(reader);
            logger.debug("RCPT TO response: {}", rcptResponse);

            // 5. Send QUIT
            writer.println("QUIT");
            String quitResponse = readResponse(reader);
            logger.debug("QUIT response: {}", quitResponse);

            boolean isValid = isResponseValid(rcptResponse, "250");
            logger.debug("Verification result for {}: {}", email, isValid ? "VALID" : "INVALID");
            return isValid;
        } catch (IOException e) {
            logger.error("SMTP verification error for email: {}", email, e);
            throw e;
        }
    }

    private String readResponse(BufferedReader reader) throws IOException {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
            if (line.length() < 4 || line.charAt(3) != '-') {
                break;
            }
            response.append("\n");
        }
        return response.toString();
    }

    private boolean isResponseValid(String response, String expectedCode) {
        boolean isValid = response.startsWith(expectedCode);
        if (!isValid) {
            logger.debug("Response validation failed. Expected: {}, Actual: {}", expectedCode, response);
        }
        return isValid;
    }

    private String getNextHeloDomain() {
        return selectNextFromList(HELO_DOMAINS, heloDomainIndex);
    }

    private String getNextVerificationEmail() {
        return selectNextFromList(VERIFICATION_EMAILS, verificationEmailIndex);
    }

    private String selectNextFromList(List<String> list, AtomicInteger index) {
        return list.get(index.getAndUpdate(i -> (i + 1) % list.size()));
    }
}