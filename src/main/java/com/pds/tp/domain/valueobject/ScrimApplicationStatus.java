package com.pds.tp.domain.valueobject;

import java.util.Locale;
import java.util.Objects;

public final class ScrimApplicationStatus {
    public static final ScrimApplicationStatus PENDING = new ScrimApplicationStatus("PENDING");
    public static final ScrimApplicationStatus ACCEPTED = new ScrimApplicationStatus("ACCEPTED");
    public static final ScrimApplicationStatus REJECTED = new ScrimApplicationStatus("REJECTED");

    private final String value;

    private ScrimApplicationStatus(String value) {
        this.value = value;
    }

    public static ScrimApplicationStatus of(String rawValue) {
        String normalized = normalize(rawValue);
        return switch (normalized) {
            case "PENDING" -> PENDING;
            case "ACCEPTED" -> ACCEPTED;
            case "REJECTED" -> REJECTED;
            default -> throw new IllegalArgumentException("Unsupported scrim application status: " + rawValue);
        };
    }

    public String value() {
        return value;
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isAccepted() {
        return this == ACCEPTED;
    }

    public boolean isRejected() {
        return this == REJECTED;
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
        if (!(obj instanceof ScrimApplicationStatus other)) {
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
            throw new IllegalArgumentException("Scrim application status cannot be blank.");
        }
        return rawValue.trim().toUpperCase(Locale.ROOT);
    }
}



