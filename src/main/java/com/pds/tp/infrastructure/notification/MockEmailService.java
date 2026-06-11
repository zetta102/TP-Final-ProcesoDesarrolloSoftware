package com.pds.tp.infrastructure.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Mock implementation of EmailService that logs email delivery instead of
 * sending real emails. In production, replace with SendGrid/JavaMail integration.
 */
@Slf4j
@Service
public class MockEmailService implements EmailService {

    @Override
    public void sendVerificationEmail(String toEmail, String username, String verificationToken) {
        String verificationLink = String.format("http://localhost:8081/v1/api/auth/%s/verify-email?token=%s",
                username, verificationToken);

        log.info("[MOCK EMAIL] Enviando email de verificación a: {}", toEmail);
        log.info("[MOCK EMAIL] Asunto: Verificá tu cuenta en eScrims");
        log.info("[MOCK EMAIL] Cuerpo: Hola {}, hacé click en el siguiente link para verificar tu email: {}",
                username, verificationLink);
        log.info("[MOCK EMAIL] Token de verificación: {}", verificationToken);
    }
}

