package com.pds.tp.application.dto;

public record ReportApplication(
        String reportingPlayerUsername,
        String lobbyId,
        String reportedPlayerUsername,
        String reason
) {
}


