package com.pds.tp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.UUID;

@Entity
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID scrimId;
    private String playerName;
    private String reason;
    private String description;
    private String status;
    private String reportedAt;
    private String resolvedAt;
    private String resolutionDetails;
}
