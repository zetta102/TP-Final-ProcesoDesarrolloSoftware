package com.pds.tp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.UUID;


@Entity
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String username;
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

    public Player(UUID id, String username, String password, String preferredRole, String region, String platform, String availability, String visibleRank, int rank, int gamesPlayed, int wins, int losses, double kda) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.preferredRole = preferredRole;
        this.region = region;
        this.platform = platform;
        this.availability = availability;
        this.visibleRank = visibleRank;
        this.rank = rank;
        this.gamesPlayed = gamesPlayed;
        this.wins = wins;
        this.losses = losses;
        this.kda = kda;
    }

    public Player(String username, String password, String preferredRole, String region, String platform, String availability) {
        this.username = username;
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

    public Player() {
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getPreferredRole() {
        return preferredRole;
    }

    public String getRegion() {
        return region;
    }

    public String getPlatform() {
        return platform;
    }

    public String getAvailability() {
        return availability;
    }

    public String getVisibleRank() {
        return visibleRank;
    }

    public int getRank() {
        return rank;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getWins() {
        return wins;
    }

    public int getLosses() {
        return losses;
    }

    public double getKda() {
        return kda;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Player player = (Player) o;

        return new EqualsBuilder().append(id, player.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("username", username)
                .append("password", password)
                .append("preferredRole", preferredRole)
                .append("region", region)
                .append("platform", platform)
                .append("availability", availability)
                .append("visibleRank", visibleRank)
                .append("rank", rank)
                .append("gamesPlayed", gamesPlayed)
                .append("wins", wins)
                .append("losses", losses)
                .append("kda", kda)
                .toString();
    }
}
