package com.lovelace.emailverifier.service.smtp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

@Component
public abstract class SmtpVerifier {
    protected static final int SMTP_PORT = 25;
    protected static final int TIMEOUT_MS = 5000;
    private static final Logger logger = LoggerFactory.getLogger(SmtpVerifier.class);

    /**
     * Verifies an email address by attempting to connect to each MX server.
     *
     * @param email     The email address to verify.
     * @param mxServers The list of MX servers to attempt verification against.
     * @return True if verification is successful with any server, otherwise false.
     */
    public final boolean verify(String email, List<String> mxServers) {
        for (String mxHost : mxServers) {
            if (connectAndVerify(email, mxHost)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Attempts to connect and verify the email using an SMTP connection.
     *
     * @param email  The email address to verify.
     * @param mxHost The MX server host to connect to.
     * @return True if the verification is successful, otherwise false.
     */
    private boolean connectAndVerify(String email, String mxHost) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(mxHost, SMTP_PORT), TIMEOUT_MS);
            return performSmtpVerification(email, socket);
        } catch (IOException e) {
            logger.warn("Could not connect to MX server {} for email {}: {}", mxHost, email, e.getMessage());
            return false;
        }
    }

    /**
     * Performs SMTP verification. Needs to be implemented by a subclass.
     *
     * @param email  The email address to verify.
     * @param socket The socket connected to the SMTP server.
     * @return True if the SMTP verification is successful, otherwise false.
     * @throws IOException If an I/O error occurs during verification.
     */
    protected abstract boolean performSmtpVerification(String email, Socket socket) throws IOException;
}
