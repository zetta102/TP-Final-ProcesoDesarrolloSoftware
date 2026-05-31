package com.pds.tp.application.dto;

public record FindLobbyData(
        String region,
        String minRank,
        String maxRank,
        String fecha,
        int maxPing
) {
}


