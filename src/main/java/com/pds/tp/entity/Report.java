package com.pds.tp.entity;

import jakarta.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.UUID;


@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    private Scrim scrimId;
    @OneToOne
    private Player playerName;
    private String reason;
    private String description;
    private String status;
    private String reportedAt;
    private String resolvedAt;
    private String resolutionDetails;

    public Report(UUID id, Scrim scrimId, Player playerName, String reason, String description, String status, String reportedAt, String resolvedAt, String resolutionDetails) {
        this.id = id;
        this.scrimId = scrimId;
        this.playerName = playerName;
        this.reason = reason;
        this.description = description;
        this.status = status;
        this.reportedAt = reportedAt;
        this.resolvedAt = resolvedAt;
        this.resolutionDetails = resolutionDetails;
    }

    public Report() {
    }

    public UUID getId() {
        return id;
    }

    public Scrim getScrimId() {
        return scrimId;
    }

    public Player getPlayerName() {
        return playerName;
    }

    public String getReason() {
        return reason;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getReportedAt() {
        return reportedAt;
    }

    public String getResolvedAt() {
        return resolvedAt;
    }

    public String getResolutionDetails() {
        return resolutionDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        Report report = (Report) o;

        return new EqualsBuilder().append(id, report.id).isEquals();
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
                .append("playerName", playerName)
                .append("reason", reason)
                .append("description", description)
                .append("status", status)
                .append("reportedAt", reportedAt)
                .append("resolvedAt", resolvedAt)
                .append("resolutionDetails", resolutionDetails)
                .toString();
    }
}
