package com.pds.tp.domain.valueobject;

import java.util.Locale;
import java.util.Objects;

public final class NotificationChannel {
    public static final NotificationChannel PUSH = new NotificationChannel("PUSH");
    public static final NotificationChannel EMAIL = new NotificationChannel("EMAIL");
    public static final NotificationChannel DISCORD = new NotificationChannel("DISCORD");
    public static final NotificationChannel SLACK = new NotificationChannel("SLACK");

    private final String value;

    private NotificationChannel(String value) {
        this.value = value;
    }

    public static NotificationChannel of(String rawValue) {
        String normalized = normalize(rawValue);
        return switch (normalized) {
            case "PUSH" -> PUSH;
            case "EMAIL" -> EMAIL;
            case "DISCORD" -> DISCORD;
            case "SLACK" -> SLACK;
            default -> throw new IllegalArgumentException("Unsupported notification channel: " + rawValue);
        };
    }

    public String value() {
        return value;
    }

    public boolean isPush() {
        return this == PUSH;
    }

    public boolean isEmail() {
        return this == EMAIL;
    }

    public boolean isDiscord() {
        return this == DISCORD;
    }

    public boolean isSlack() {
        return this == SLACK;
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
        if (!(obj instanceof NotificationChannel other)) {
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
            throw new IllegalArgumentException("Notification channel cannot be blank.");
        }
        return rawValue.trim().toUpperCase(Locale.ROOT);
    }
}



