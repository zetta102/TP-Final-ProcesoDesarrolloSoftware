package com.pds.tp.repository;

import com.pds.tp.entity.Scrim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScrimRepository extends JpaRepository<Scrim, UUID> {
}
