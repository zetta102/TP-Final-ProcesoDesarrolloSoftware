package com.pds.tp.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Postulacion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Player usuario;

    @ManyToOne
    private Scrim scrim;

    private String rolDeseado;

    @Setter
    @Enumerated(EnumType.STRING)
    private PostulacionEstado estado = PostulacionEstado.PENDIENTE;
}

