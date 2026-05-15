package com.pds.tp.entity;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
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
    private int maxPing;
    private String gameMode;
    private String map;
    private String status;
    @OneToOne
    private Player host;
    @OneToMany
    private List<Player> players;

    public Lobby(UUID id, String scheduledTime, int maxPlayers, int minPlayers, String region, String minRank, String maxRank, int maxPing, String gameMode, String map, String status, Player host, List<Player> players) {
        this.id = id;
        this.scheduledTime = scheduledTime;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.region = region;
        this.minRank = minRank;
        this.maxRank = maxRank;
        this.maxPing = maxPing;
        this.gameMode = gameMode;
        this.map = map;
        this.status = status;
        this.host = host;
        this.players = players;
    }

    public Lobby(String scheduledTime, int maxPlayers, int minPlayers, String region, String minRank, String maxRank, int maxPing, String gameMode, String map, String status, Player host, List<Player> players) {
        this.scheduledTime = scheduledTime;
        this.maxPlayers = maxPlayers;
        this.minPlayers = minPlayers;
        this.region = region;
        this.minRank = minRank;
        this.maxRank = maxRank;
        this.maxPing = maxPing;
        this.gameMode = gameMode;
        this.map = map;
        this.status = status;
        this.host = host;
        this.players = players;
    }

    public Lobby() {

    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinPlayers() {
        return minPlayers;
    }

    public String getRegion() {
        return region;
    }

    public String getMinRank() {
        return minRank;
    }

    public String getMaxRank() {
        return maxRank;
    }

    public int getMaxPing() {
        return maxPing;
    }

    public String getGameMode() {
        return gameMode;
    }

    public String getMap() {
        return map;
    }

    public String getStatus() {
        return status;
    }

    public Player getHost() {
        return host;
    }

    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Lobby lobby = (Lobby) o;

        return new EqualsBuilder().append(id, lobby.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("scheduledTime", scheduledTime)
                .append("maxPlayers", maxPlayers)
                .append("minPlayers", minPlayers)
                .append("region", region)
                .append("minRank", minRank)
                .append("maxRank", maxRank)
                .append("maxPing", maxPing)
                .append("gameMode", gameMode)
                .append("status", status)
                .append("host", host)
                .append("players", players)
                .toString();
    }
}
