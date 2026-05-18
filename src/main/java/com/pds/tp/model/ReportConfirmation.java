package com.pds.tp.model;

import java.util.UUID;

public record ReportConfirmation(
        UUID reportId,
        String reportingPlayerUsername,
        String lobbyId,
        String reportedPlayerUsername,
        String status
) {
}
