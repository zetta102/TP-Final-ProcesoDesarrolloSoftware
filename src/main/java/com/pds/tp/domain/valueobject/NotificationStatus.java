package com.pds.tp.domain.valueobject;

import java.util.Locale;
import java.util.Objects;

public final class NotificationStatus {
    public static final NotificationStatus PENDING = new NotificationStatus("PENDING");
    public static final NotificationStatus SENT = new NotificationStatus("SENT");
    public static final NotificationStatus FAILED = new NotificationStatus("FAILED");

    private final String value;

    private NotificationStatus(String value) {
        this.value = value;
    }

    public static NotificationStatus of(String rawValue) {
        String normalized = normalize(rawValue);
        return switch (normalized) {
            case "PENDING" -> PENDING;
            case "SENT" -> SENT;
            case "FAILED", "ERROR" -> FAILED;
            default -> throw new IllegalArgumentException("Unsupported notification status: " + rawValue);
        };
    }

    public String value() {
        return value;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isSent() {
        return this == SENT;
    }

    public boolean isFailed() {
        return this == FAILED;
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
        if (!(obj instanceof NotificationStatus other)) {
            return false;
        }
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    private static String normalize(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new IllegalArgumentException("Notification status cannot be blank.");
        }
        return rawValue.trim().toUpperCase(Locale.ROOT);
    }
}



