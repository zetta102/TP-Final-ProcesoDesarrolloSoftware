package com.pds.tp.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Equipo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String lado;

    @ManyToOne
    private Scrim scrim;

    @ManyToMany
    private List<Player> jugadores = new ArrayList<>();
}

