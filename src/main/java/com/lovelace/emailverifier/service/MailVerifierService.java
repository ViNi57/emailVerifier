package com.lovelace.emailverifier.service;

import com.lovelace.emailverifier.service.domain.EmailValidationResult;
import com.lovelace.emailverifier.service.smtp.SmtpVerifier;
import com.lovelace.emailverifier.service.strategy.EmailValidationStrategy;
import com.lovelace.emailverifier.util.EmailUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MailVerifierService {
    private static final Logger logger = LoggerFactory.getLogger(MailVerifierService.class);
    private final Map<String, List<String>> mxRecordsCache;
    private final List<EmailValidationStrategy> validationStrategies;
    private final DnsLookupService dnsLookupService;
    private final SmtpVerifier smtpVerifier;

    public MailVerifierService(List<EmailValidationStrategy> validationStrategies,
                               DnsLookupService dnsLookupService,
                               SmtpVerifier smtpVerifier,
                               Map<String, List<String>> mxRecordsCache) {
        this.validationStrategies = validationStrategies;
        this.dnsLookupService = dnsLookupService;
        this.smtpVerifier = smtpVerifier;
        this.mxRecordsCache = mxRecordsCache;
    }

    public String verifyEmail(String email) {
        for (EmailValidationStrategy strategy : validationStrategies) {
            EmailValidationResult result = strategy.validate(email);
            if (!result.isValid()) {
                return result.message();
            }
        }
        try {
            String domain = EmailUtils.extractDomain(email);
            List<String> mxServers = mxRecordsCache.get(domain);

            return smtpVerifier.verify(email, mxServers)
                    ? "Valid email address"
                    : "Invalid email address";
        } catch (Exception e) {
            logger.error("Error occurred while verifying email: {}", e.getMessage());
        }
        return Strings.EMPTY;
    }
}