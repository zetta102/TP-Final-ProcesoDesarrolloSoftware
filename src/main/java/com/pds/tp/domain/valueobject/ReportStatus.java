package com.pds.tp.domain.valueobject;

import java.util.Locale;
import java.util.Objects;

public final class ReportStatus {
    public static final ReportStatus PENDIENTE = new ReportStatus("PENDIENTE");
    public static final ReportStatus RESUELTO_AUTO = new ReportStatus("RESUELTO_AUTO");
    public static final ReportStatus RESUELTO_BOT = new ReportStatus("RESUELTO_BOT");
    public static final ReportStatus ESCALADO_HUMANO = new ReportStatus("ESCALADO_HUMANO");

    private final String value;

    private ReportStatus(String value) {
        this.value = value;
    }

    public static ReportStatus of(String rawValue) {
        String normalized = normalize(rawValue);
        return switch (normalized) {
            case "PENDIENTE", "CREATED", "PENDING" -> PENDIENTE;
            case "RESUELTO_AUTO", "AUTO_RESOLVED" -> RESUELTO_AUTO;
            case "RESUELTO_BOT", "RESOLVED_BOT" -> RESUELTO_BOT;
            case "ESCALADO_HUMANO", "ESCALATED_HUMAN" -> ESCALADO_HUMANO;
            default -> throw new IllegalArgumentException("Estado de reporte no soportado: " + rawValue);
        };
    }

    private static String normalize(String rawValue) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new IllegalArgumentException("El estado del reporte no puede estar vacío.");
        }
        return rawValue.trim().toUpperCase(Locale.ROOT);
    }

    public boolean isPendiente() {
        return this == PENDIENTE;
    }

    public boolean isResueltoAuto() {
        return this == RESUELTO_AUTO;
    }

    public boolean isResueltoBot() {
        return this == RESUELTO_BOT;
    }

    public boolean isEscaladoHumano() {
        return this == ESCALADO_HUMANO;
    }

    public boolean isResolved() {
        return isResueltoAuto() || isResueltoBot();
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
        if (!(obj instanceof ReportStatus other)) {
            return false;
        }
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}

