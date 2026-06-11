package com.pds.tp.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Represents a saved search preference for a player.
 * Used by the Observer pattern to notify players when a new scrim matches their criteria.
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class SavedSearch {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Player player;

    private String game;
    private String region;
    private String minRank;
    private String maxRank;
    private Integer maxLatency;
    private String format;

    public SavedSearch(Player player, String game, String region, String minRank, String maxRank, Integer maxLatency, String format) {
        this.player = player;
        this.game = game;
        this.region = region;
        this.minRank = minRank;
        this.maxRank = maxRank;
        this.maxLatency = maxLatency;
        this.format = format;
    }

    /**
     * Checks whether a given lobby matches this saved search criteria.
     */
    public boolean matchesLobby(Lobby lobby) {
        if (game != null && !game.isBlank() && !game.equalsIgnoreCase(lobby.getGameMode())) {
            return false;
        }
        if (region != null && !region.isBlank() && !region.equalsIgnoreCase(lobby.getRegion())) {
            return false;
        }
        if (maxLatency != null && lobby.getMaxPing() > maxLatency) {
            return false;
        }
        return true;
    }
}

