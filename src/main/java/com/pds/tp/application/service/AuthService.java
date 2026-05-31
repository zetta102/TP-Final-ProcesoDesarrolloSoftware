package com.pds.tp.application.service;

import com.pds.tp.application.dto.PlayerData;
import com.pds.tp.domain.entity.EmailVerificationStatus;
import com.pds.tp.domain.entity.Player;
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
        if (playerRepository.existsByUsername(playerData.playerName())) {
            throw new IllegalArgumentException("El username ya está registrado.");
        }
        if (playerRepository.existsByEmail(playerData.email())) {
            throw new IllegalArgumentException("El email ya está registrado.");
        }

        String hashedPassword = passwordEncoder.encode(playerData.password());
        Player player = new Player(
                playerData.playerName(),
                playerData.email(),
                hashedPassword,
                playerData.preferredRole(),
                playerData.region(),
                playerData.platform(),
                playerData.availability()
        );
        log.info("Registered new player: {}", player.getUsername());
        return playerRepository.save(player);
    }

    public boolean authenticate(String identifier, String password) {
        Player player = resolvePlayer(identifier);
        if (player == null) {
            return false;
        }
        if (player.getEmailVerificationStatus() != EmailVerificationStatus.VERIFICADO) {
            return false;
        }
        return passwordEncoder.matches(password, player.getPassword());
    }

    private Player resolvePlayer(String identifier) {
        if (identifier == null || identifier.isBlank()) {
            return null;
        }
        if (identifier.contains("@")) {
            return playerRepository.findByEmail(identifier);
        }
        return playerRepository.findByUsername(identifier);
    }

    public String verifyEmail(String username) {
        Player player = playerRepository.findByUsername(username);
        if (player == null) {
            return "Usuario no encontrado.";
        }
        if (player.getEmailVerificationStatus() == EmailVerificationStatus.VERIFICADO) {
            return "El email ya estaba verificado.";
        }

        player.setEmailVerificationStatus(EmailVerificationStatus.VERIFICADO);
        playerRepository.save(player);
        return "Email verificado con éxito.";
    }
}

