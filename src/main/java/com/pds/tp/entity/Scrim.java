package com.pds.tp.entity;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
public class Scrim {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String lobbyId;
    private String gameMode;
    private String map;
    @OneToMany
    private List<Player> team1;
    @OneToMany
    private List<Player> team2;

}
