package com.pds.tp.model;

public record LobbyApplication(
        String username,
        String lobbyId,
        String desiredRole
) {
}
