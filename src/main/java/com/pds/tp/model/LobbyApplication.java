package com.pds.tp.model;

public record LobbyApplication(
        String playerId,
        String lobbyId,
        String status,
        String desiredRole
) {
}
