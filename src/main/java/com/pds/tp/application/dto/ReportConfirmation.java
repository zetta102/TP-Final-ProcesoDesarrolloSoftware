package com.pds.tp.application.dto;

import com.pds.tp.domain.valueobject.ReportStatus;

import java.util.UUID;

public record ReportConfirmation(
        UUID reportId,
        String reportingPlayerUsername,
        String lobbyId,
        String reportedPlayerUsername,
        ReportStatus status
) {
}
