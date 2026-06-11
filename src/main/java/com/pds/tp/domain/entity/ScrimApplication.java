package com.pds.tp.domain.entity;

import com.pds.tp.domain.valueobject.ScrimApplicationStatus;
import com.pds.tp.infrastructure.persistence.converter.ScrimApplicationStatusConverter;
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
public class ScrimApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Player user;

    @ManyToOne
    private Scrim scrim;

    private String desiredRole;

    @Setter
    @Convert(converter = ScrimApplicationStatusConverter.class)
    private ScrimApplicationStatus status = ScrimApplicationStatus.PENDING;
}


