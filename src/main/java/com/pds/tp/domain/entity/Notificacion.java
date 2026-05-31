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
public class Notificacion {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String tipo;
    private String payload;

    @Enumerated(EnumType.STRING)
    private CanalNotificacion canal;

    @Setter
    @Enumerated(EnumType.STRING)
    private EstadoNotificacion estado = EstadoNotificacion.PENDIENTE;
}

