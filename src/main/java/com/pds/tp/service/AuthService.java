package com.pds.tp.service;

import com.pds.tp.entity.Player;
import com.pds.tp.model.PlayerData;
import com.pds.tp.repository.PlayerRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Player register(PlayerData playerData) {
        // Hash the password before saving
        String hashedPassword = passwordEncoder.encode(playerData.password());
        Player player = new Player(
                playerData.playerName(),
                hashedPassword,
                playerData.preferredRole(),
                playerData.region(),
                playerData.platform(),
                playerData.availability()
        );
        return playerRepository.save(player);
    }

    public boolean authenticate(String username, String rawPassword) {
        Player player = playerRepository.findByUsername(username);
        if (player == null) return false;

        // Compare the raw password with the hashed password in the DB
        return passwordEncoder.matches(rawPassword, player.getPassword());
    }
}