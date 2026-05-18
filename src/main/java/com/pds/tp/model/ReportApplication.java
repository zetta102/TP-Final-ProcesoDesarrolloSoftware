package com.pds.tp.model;

public record ReportApplication(
        String reportingPlayerUsername,
        String lobbyId,
        String reportedPlayerUsername,
        String reason
) {
}
