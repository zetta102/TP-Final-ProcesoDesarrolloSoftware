package com.pds.tp.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Per-player statistics for a specific scrim (kills, deaths, assists, MVP).
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class PlayerScrimStats {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private ScrimStatistics scrimStatistics;

    @ManyToOne
    private Player player;

    private int kills;
    private int deaths;
    private int assists;
    private boolean mvp;

    public PlayerScrimStats(ScrimStatistics scrimStatistics, Player player, int kills, int deaths, int assists, boolean mvp) {
        this.scrimStatistics = scrimStatistics;
        this.player = player;
        this.kills = kills;
        this.deaths = deaths;
        this.assists = assists;
        this.mvp = mvp;
    }

    public double getKdaRatio() {
        return deaths == 0 ? (kills + assists) : (double) (kills + assists) / deaths;
    }
}

