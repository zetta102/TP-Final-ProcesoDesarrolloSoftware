package com.pds.tp.application.dto;

import java.util.List;

public record CreateStatisticsRequest(
        String winningTeam,
        String status,
        List<PlayerStatsEntry> playerStats
) {
    public record PlayerStatsEntry(
            String username,
            int kills,
            int deaths,
            int assists,
            boolean mvp
    ) {
    }
}
