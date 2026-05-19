package com.pds.tp.model;

public record FindLobbyData(
        String region,
        String minRank,
        String maxRank,
        String fecha,
        int maxPing
) {
}
