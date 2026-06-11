package com.pds.tp.application.dto;

public record SavedSearchRequest(
        String game,
        String region,
        String minRank,
        String maxRank,
        Integer maxLatency,
        String format
) {
}

