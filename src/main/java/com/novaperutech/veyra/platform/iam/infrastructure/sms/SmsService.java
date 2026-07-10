package com.novaperutech.veyra.platform.iam.infrastructure.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Sends SMS one-time codes via Twilio for SMS-based MFA.
 * Authenticates with a Restricted API Key (recommended by Twilio over the master Auth Token).
 */
@Service
@Slf4j
public class SmsService {

    @Value("${twilio.account-sid}")
    private String accountSid;

    @Value("${twilio.api-key-sid}")
    private String apiKeySid;

    @Value("${twilio.api-key-secret}")
    private String apiKeySecret;

    @Value("${twilio.from-number}")
    private String fromNumber;

    @PostConstruct
    public void init() {
        Twilio.init(apiKeySid, apiKeySecret, accountSid);
        log.info("Twilio SMS service initialized. accountSid={}, apiKeySid={}, fromNumber={}",
                maskForLog(accountSid), maskForLog(apiKeySid), fromNumber);
    }

    public void sendMfaCode(String toPhoneNumber, String code) {
        try {
            Message.creator(
                    new PhoneNumber(toPhoneNumber),
                    new PhoneNumber(fromNumber),
                    "Your Veyra verification code is: " + code
            ).create();
            log.info("MFA SMS code sent to {}", maskPhoneNumber(toPhoneNumber));
        } catch (Exception e) {
            log.error("Error sending MFA SMS to {}: {}", maskPhoneNumber(toPhoneNumber), e.getMessage(), e);
            // TEMPORARY: surface the real cause in the API response for remote diagnosis during TB2 testing.
            // Revert to a generic message before final submission.
            throw new IllegalStateException("Failed to send SMS verification code: " + e.getClass().getSimpleName() + ": " + e.getMessage(), e);
        }
    }

    private String maskForLog(String value) {
        if (value == null || value.isBlank()) return "<empty>";
        if (value.length() <= 6) return "***";
        return value.substring(0, 6) + "...";
    }

    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) return "****";
        return "****" + phoneNumber.substring(phoneNumber.length() - 4);
    }
}
