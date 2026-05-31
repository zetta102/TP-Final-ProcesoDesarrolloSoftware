package com.pds.tp.infrastructure.repository;

import com.pds.tp.domain.entity.Scrim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScrimRepository extends JpaRepository<Scrim, UUID> {
}


