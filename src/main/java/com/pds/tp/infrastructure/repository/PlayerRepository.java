package com.pds.tp.infrastructure.repository;

import com.pds.tp.domain.entity.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlayerRepository extends JpaRepository<Player, UUID> {
    boolean existsByUsernameAndPassword(String username, String password);

    Player findByUsername(String username);

}


