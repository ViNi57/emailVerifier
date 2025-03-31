package com.lovelace.emailverifier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.*;

@Service
public class DnsLookupService {
    private static final Logger logger = LoggerFactory.getLogger(DnsLookupService.class);

    public List<String> getMxServers(String domain) {
        try {
            DirContext dnsContext = new InitialDirContext(getEnvironmentProperties());
            Attributes domainAttributes = dnsContext.getAttributes("dns:/" + domain, new String[]{"MX"});
            Attribute mailExchangeAttribute = domainAttributes.get("MX");

            if (Objects.nonNull(mailExchangeAttribute)) {
                return extractAndSortMailExchangeRecords(mailExchangeAttribute);
            }
        } catch (NamingException e) {
            logger.error("Failed to lookup MX records for domain: {}", domain, e);
        }

        return Collections.emptyList();
    }

    private Properties getEnvironmentProperties() {
        Properties environmentProperties = new Properties();
        environmentProperties.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.dns.DnsContextFactory");
        return environmentProperties;
    }

    private List<String> extractAndSortMailExchangeRecords(Attribute mailExchangeAttribute) throws NamingException {
        List<String> mailExchangeRecords = new ArrayList<>();
        NamingEnumeration<?> recordsEnumeration = mailExchangeAttribute.getAll();
        while (recordsEnumeration.hasMore()) {
            String record = (String) recordsEnumeration.next();
            mailExchangeRecords.add(record);
        }

        return mailExchangeRecords.stream()
                .sorted(Comparator.comparingInt(this::extractMailExchangePriority))
                .map(this::stripPriorityFromMailExchangeRecord)
                .filter(Objects::nonNull)
                .toList();
    }

    private int extractMailExchangePriority(String mailExchangeRecord) {
        try {
            return Integer.parseInt(mailExchangeRecord.split(" ")[0]);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    private String stripPriorityFromMailExchangeRecord(String mailExchangeRecord) {
        String[] parts = mailExchangeRecord.split(" ");
        return (parts.length >= 2) ? parts[1].replaceFirst("\\.$", "") : null;
    }
}