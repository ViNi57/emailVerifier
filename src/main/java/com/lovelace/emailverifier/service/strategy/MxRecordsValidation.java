package com.lovelace.emailverifier.service.strategy;

import com.lovelace.emailverifier.service.DnsLookupService;
import com.lovelace.emailverifier.service.domain.EmailValidationResult;
import com.lovelace.emailverifier.util.EmailUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class MxRecordsValidation implements EmailValidationStrategy {
    private final DnsLookupService dnsLookupService;
    private final Map<String, List<String>> mxRecordsCache;

    public MxRecordsValidation(DnsLookupService dnsLookupService, Map<String, List<String>> mxRecordsCache) {
        this.dnsLookupService = dnsLookupService;
        this.mxRecordsCache = mxRecordsCache;
    }

    @Override
    public EmailValidationResult validate(String email) {
        String domain = EmailUtils.extractDomain(email);
        mxRecordsCache.computeIfAbsent(domain, key -> new ArrayList<>()).addAll(dnsLookupService.getMxServers(domain));
        return !mxRecordsCache.get(domain).isEmpty()
                ? EmailValidationResult.valid()
                : EmailValidationResult.invalid("Domain does not have valid MX records");

    }
}