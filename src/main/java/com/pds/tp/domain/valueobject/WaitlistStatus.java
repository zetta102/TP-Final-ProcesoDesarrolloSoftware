package com.pds.tp.domain.valueobject;

import java.util.Locale;
import java.util.Objects;

public final class WaitlistStatus {
    public static final WaitlistStatus PENDING = new WaitlistStatus("PENDING");
    public static final WaitlistStatus PROMOTED = new WaitlistStatus("PROMOTED");

    private final String value;

    private WaitlistStatus(String value) {
        this.value = value;
    }

    public static WaitlistStatus of(String rawValue) {
        String normalized = normalize(rawValue);
        return switch (normalized) {
            case "PENDING", "PENDIENTE" -> PENDING;
            case "PROMOTED", "PROMOVIDO" -> PROMOTED;
            default -> throw new IllegalArgumentException("Estado de lista de espera no soportado: " + rawValue);
        };
    }

    private static String normalize(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new IllegalArgumentException("El estado de lista de espera no puede estar vacío.");
        }
        return rawValue.trim().toUpperCase(Locale.ROOT);
    }

    public boolean isPending() {
        return this == PENDING;
    }

    public boolean isPromoted() {
        return this == PROMOTED;
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
        if (!(obj instanceof WaitlistStatus other)) {
            return false;
        }
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}


