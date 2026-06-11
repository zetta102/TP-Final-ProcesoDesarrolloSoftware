package com.pds.tp.domain.valueobject;

import java.util.Locale;
import java.util.Objects;

public final class UserRole {
    public static final UserRole USER = new UserRole("USER");
    public static final UserRole MODERATOR = new UserRole("MODERATOR");
    public static final UserRole ADMIN = new UserRole("ADMIN");

    private final String value;

    private UserRole(String value) {
        this.value = value;
    }

    public static UserRole of(String rawValue) {
        String normalized = normalize(rawValue);
        return switch (normalized) {
            case "USER" -> USER;
            case "MODERATOR", "MOD" -> MODERATOR;
            case "ADMIN" -> ADMIN;
            default -> throw new IllegalArgumentException("Rol no soportado: " + rawValue);
        };
    }

    public String value() {
        return value;
    }

    public boolean isUser() {
        return this == USER;
    }

    public boolean isModerator() {
        return this == MODERATOR;
    }

    public boolean isAdmin() {
        return this == ADMIN;
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
        if (!(obj instanceof UserRole other)) {
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
            throw new IllegalArgumentException("El rol no puede estar vacío.");
        }
        return rawValue.trim().toUpperCase(Locale.ROOT);
    }
}


