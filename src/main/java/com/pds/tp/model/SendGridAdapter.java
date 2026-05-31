package com.pds.tp.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SendGridAdapter implements Notifier {
    @Override
    public void sendNotification(String emailAddress, String message) {
        // Here we would use the SendGrid Java SDK
        log.info("[SendGrid API] Enviando email a {}. Cuerpo: {}", emailAddress, message);
    }
}