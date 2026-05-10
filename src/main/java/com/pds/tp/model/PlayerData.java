package com.pds.tp.model;

public record PlayerData(
        String playerName,
        String password,
        String preferredRole,
        String region,
        String platform,
        String availability
) {
}
