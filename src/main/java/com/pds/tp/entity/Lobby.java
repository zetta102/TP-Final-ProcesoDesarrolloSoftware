package com.pds.tp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class Lobby {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String scheduledTime;
    private int maxPlayers;
    private int minPlayers;
    private String region;
    private String minRank;
    private String maxRank;
    private String maxPing;
    private String gameMode;
    private String status;
}
