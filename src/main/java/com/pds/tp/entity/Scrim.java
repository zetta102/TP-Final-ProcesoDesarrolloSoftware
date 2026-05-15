package com.pds.tp.entity;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
public class Scrim {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @OneToOne
    private Lobby lobbyId;
    private String gameMode;
    private String map;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;

    public Scrim(UUID id, Lobby lobbyId, String gameMode, String map, LocalDateTime startTime, LocalDateTime endTime, String status) {
        this.id = id;
        this.lobbyId = lobbyId;
        this.gameMode = gameMode;
        this.map = map;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public Scrim(String gameMode, String map, LocalDateTime startTime, String status) {
        this.gameMode = gameMode;
        this.map = map;
        this.startTime = startTime;
        this.status = status;
    }

    public Scrim() {
    }

    public UUID getId() {
        return id;
    }

    public Lobby getLobbyId() {
        return lobbyId;
    }

    public String getGameMode() {
        return gameMode;
    }

    public String getMap() {
        return map;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Scrim scrim = (Scrim) o;

        return new EqualsBuilder().append(id, scrim.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("lobbyId", lobbyId)
                .append("gameMode", gameMode)
                .append("map", map)
                .append("startTime", startTime)
                .append("endTime", endTime)
                .append("status", status)
                .toString();
    }

}
