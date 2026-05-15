package com.pds.tp.entity;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;
import java.util.UUID;

public class ScrimStatistics {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @OneToOne
    private Scrim scrimId;
    @OneToMany
    private List<Player> redTeam;
    @OneToMany
    private List<Player> blueTeam;
    private String winningTeam;

    public ScrimStatistics(UUID id, Scrim scrimId, List<Player> redTeam, List<Player> blueTeam, String winningTeam) {
        this.id = id;
        this.scrimId = scrimId;
        this.redTeam = redTeam;
        this.blueTeam = blueTeam;
        this.winningTeam = winningTeam;
    }


    public ScrimStatistics(Scrim scrimId, List<Player> redTeam, List<Player> blueTeam, String winningTeam) {
        this.scrimId = scrimId;
        this.redTeam = redTeam;
        this.blueTeam = blueTeam;
        this.winningTeam = winningTeam;
    }

    public ScrimStatistics() {


    }

    public UUID getId() {
        return id;
    }

    public Scrim getScrimId() {
        return scrimId;
    }

    public List<Player> getRedTeam() {
        return redTeam;
    }

    public List<Player> getBlueTeam() {
        return blueTeam;
    }

    public String getWinningTeam() {
        return winningTeam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ScrimStatistics that = (ScrimStatistics) o;

        return new EqualsBuilder().append(id, that.id).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("id", id)
                .append("scrimId", scrimId)
                .append("redTeam", redTeam)
                .append("blueTeam", blueTeam)
                .append("winningTeam", winningTeam)
                .toString();
    }
}
