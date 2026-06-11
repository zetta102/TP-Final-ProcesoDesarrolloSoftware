package com.pds.tp.domain.builder;

import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import com.pds.tp.domain.validation.GameValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Builder used by services to assemble a Lobby/Scrim setup with business defaults.
 */
public class ScrimBuilder {
    private LocalDateTime scheduledTime = LocalDateTime.now();
    private int maxPlayers;
    private int minPlayers;
    private String region;
    private String minRank;
    private String maxRank;
    private int maxPing;
    private String gameMode;
    private String map;
    private String duration;
    private String modality;
    private Player host;
    private GameValidator gameValidator;

    public ScrimBuilder host(Player host) {
        this.host = host;
        this.region = host != null ? host.getRegion() : null;
        return this;
    }

    public ScrimBuilder region(String region) {
        if (region != null && !region.isBlank()) {
            this.region = region;
        }
        return this;
    }

    public ScrimBuilder fecha(LocalDateTime scheduledTime) {
        if (scheduledTime != null) {
            this.scheduledTime = scheduledTime;
        }
        return this;
    }

    public ScrimBuilder formato(int minPlayers, int maxPlayers) {
        this.minPlayers = minPlayers;
        this.maxPlayers = maxPlayers;
        return this;
    }

    public ScrimBuilder rango(String minRank, String maxRank) {
        this.minRank = minRank;
        this.maxRank = maxRank;
        return this;
    }

    public ScrimBuilder juego(String gameMode, String map) {
        this.gameMode = gameMode;
        this.map = map;
        return this;
    }

    public ScrimBuilder latenciaMax(int maxPing) {
        this.maxPing = maxPing;
        return this;
    }

    public ScrimBuilder duracion(String duration) {
        this.duration = duration;
        return this;
    }

    public ScrimBuilder modalidad(String modality) {
        this.modality = modality;
        return this;
    }

    public ScrimBuilder validator(GameValidator validator) {
        this.gameValidator = validator;
        return this;
    }

    public Lobby build() {
        if (host == null) {
            throw new IllegalStateException("El host es requerido para crear el lobby.");
        }
        if (region == null || region.isBlank()) {
            throw new IllegalStateException("La región es requerida para crear el lobby.");
        }
        if (maxPlayers < minPlayers) {
            throw new IllegalStateException("El máximo de jugadores no puede ser menor al mínimo.");
        }

        ArrayList<Player> initialPlayers = new ArrayList<>();
        initialPlayers.add(host);

        Lobby lobby = new Lobby(
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
                duration,
                modality,
                host,
                initialPlayers,
                new HashSet<>()
        );

        // Template Method: validate game-specific composition rules.
        if (gameValidator != null) {
            gameValidator.validate(lobby);
        }

        return lobby;
    }
}
