package com.pds.tp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String playerName;
    private String password;
    private String preferredRole;
    private String region;
    private String platform;
    private String availability;
    private String visibleRank;
    private int rank;
    private int gamesPlayed;
    private int wins;
    private int losses;
    private double kda;

    public Player(String playerName, String password, String preferredRole, String region, String platform, String availability) {
        this.playerName = playerName;
        this.password = password;
        this.preferredRole = preferredRole;
        this.region = region;
        this.platform = platform;
        this.availability = availability;
        this.visibleRank = "BRONCE";
        this.rank = 0;
        this.gamesPlayed = 0;
        this.wins = 0;
        this.losses = 0;
        this.kda = 0.0;
    }
}
