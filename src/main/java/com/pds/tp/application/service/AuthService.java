package com.pds.tp.application.service;

import com.pds.tp.domain.entity.Player;
import com.pds.tp.application.dto.PlayerData;
import com.pds.tp.infrastructure.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Player register(PlayerData playerData) {
        String hashedPassword = passwordEncoder.encode(playerData.password());
        Player player = new Player(
                playerData.playerName(),
                hashedPassword,
                playerData.preferredRole(),
                playerData.region(),
                playerData.platform(),
                playerData.availability()
        );
        log.info("Registered new player: {}", player.getUsername());
        return playerRepository.save(player);
    }

    public boolean authenticate(String username, String password) {
        Player player = playerRepository.findByUsername(username);
        if (player == null) {
            return false;
        }
        return passwordEncoder.matches(password, player.getPassword());
    }
}

