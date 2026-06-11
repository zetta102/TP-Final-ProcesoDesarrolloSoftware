package com.pds.tp.domain.valueobject;

import java.util.Locale;
import java.util.Objects;

public final class EmailVerificationStatus {
    public static final EmailVerificationStatus PENDING = new EmailVerificationStatus("PENDING");
    public static final EmailVerificationStatus VERIFIED = new EmailVerificationStatus("VERIFIED");

    private final String value;

    private EmailVerificationStatus(String value) {
        this.value = value;
    }

    public static EmailVerificationStatus of(String rawValue) {
        String normalized = normalize(rawValue);
        return switch (normalized) {
            case "PENDING", "PENDIENTE" -> PENDING;
            case "VERIFIED", "VERIFICADO" -> VERIFIED;
            default -> throw new IllegalArgumentException("Estado de verificación de email no soportado: " + rawValue);
        };
    }

    private static String normalize(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new IllegalArgumentException("El estado de verificación de email no puede estar vacío.");
        }
        return rawValue.trim().toUpperCase(Locale.ROOT);
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isVerified() {
        return this == VERIFIED;
    }

    public boolean isUnverified() {
        return isPending();
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof EmailVerificationStatus other)) {
            return false;
        }
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}


