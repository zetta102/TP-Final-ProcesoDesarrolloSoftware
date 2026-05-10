package com.pds.tp.repository;

import com.pds.tp.entity.Lobby;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LobbyRepository extends JpaRepository<Lobby, UUID> {
}
