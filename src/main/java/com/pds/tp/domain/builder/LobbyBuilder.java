package com.pds.tp.domain.builder;

import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

public class LobbyBuilder {
    private LocalDateTime scheduledTime = LocalDateTime.now();
    private int maxPlayers;
    private int minPlayers;
    private String region;
    private String minRank;
    private String maxRank;
    private int maxPing;
    private String gameMode;
    private String map;
    private Player host;

    public LobbyBuilder conHost(Player host) {
        this.host = host;
        this.region = host != null ? host.getRegion() : null;
        return this;
    }

    public LobbyBuilder conRegion(String region) {
        if (region != null && !region.isBlank()) {
            this.region = region;
        }
        return this;
    }

    public LobbyBuilder conFecha(LocalDateTime scheduledTime) {
        if (scheduledTime != null) {
            this.scheduledTime = scheduledTime;
        }
        return this;
    }

    public LobbyBuilder conFormato(int minPlayers, int maxPlayers) {
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        return this;
    }

    public LobbyBuilder conRango(String minRank, String maxRank) {
        this.minRank = minRank;
        this.maxRank = maxRank;
        return this;
    }

    public LobbyBuilder conJuego(String gameMode, String map) {
        this.gameMode = gameMode;
        this.map = map;
        return this;
    }

    public LobbyBuilder conLatenciaMax(int maxPing) {
        this.maxPing = maxPing;
        return this;
    }

    public Lobby build() {
        if (host == null) {
            throw new IllegalStateException("El host es requerido para crear el lobby.");
        }
        if (region == null || region.isBlank()) {
            throw new IllegalStateException("La region es requerida para crear el lobby.");
        }
        if (maxPlayers < minPlayers) {
            throw new IllegalStateException("El máximo de jugadores no puede ser menor al mínimo.");
        }

        ArrayList<Player> initialPlayers = new ArrayList<>();
        initialPlayers.add(host);

        return new Lobby(
                scheduledTime,
                maxPlayers,
                minPlayers,
                region,
                minRank,
                maxRank,
                maxPing,
                gameMode,
                map,
                "Buscando",
                host,
                initialPlayers,
                new HashSet<>()
        );
    }
}

