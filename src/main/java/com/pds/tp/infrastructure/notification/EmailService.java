package com.pds.tp.infrastructure.notification;

/**
 * Abstraction for email delivery. Implementations can integrate with real SMTP/SendGrid
 * or use a mock for development/testing.
 */
public interface EmailService {
    void sendVerificationEmail(String toEmail, String username, String verificationToken);
}

