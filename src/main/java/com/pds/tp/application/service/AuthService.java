package com.pds.tp.application.service;

import com.pds.tp.application.dto.OAuthCallbackData;
import com.pds.tp.application.dto.PlayerData;
import com.pds.tp.config.JwtService;
import com.pds.tp.domain.entity.Player;
import com.pds.tp.domain.valueobject.EmailVerificationStatus;
import com.pds.tp.infrastructure.MockOAuthProvider;
import com.pds.tp.infrastructure.notification.EmailService;
import com.pds.tp.infrastructure.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class AuthService {
    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final MockOAuthProvider oAuthProvider;
    private final JwtService jwtService;

    public AuthService(PlayerRepository playerRepository, PasswordEncoder passwordEncoder,
                       EmailService emailService, MockOAuthProvider oAuthProvider,
                       JwtService jwtService) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.oAuthProvider = oAuthProvider;
        this.jwtService = jwtService;
    }

    public Player register(PlayerData playerData) {
        if (playerRepository.existsByUsername(playerData.playerName())) {
            throw new IllegalArgumentException("El nombre de usuario ya está registrado.");
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

        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();
        player.setVerificationToken(verificationToken);

        Player savedPlayer = playerRepository.save(player);
        log.info("Nuevo jugador registrado: {}", savedPlayer.getUsername());

        // Send verification email (mocked)
        emailService.sendVerificationEmail(savedPlayer.getEmail(), savedPlayer.getUsername(), verificationToken);

        return savedPlayer;
    }

    public Player registerViaOAuth(OAuthCallbackData callbackData) {
        Map<String, String> profile = oAuthProvider.validateAndFetchProfile(
                callbackData.provider(), callbackData.oauthToken());

        String username = profile.get("username");
        String email = profile.get("email");

        // If user already exists, return existing
        Player existing = playerRepository.findByEmail(email);
        if (existing != null) {
            log.info("Usuario OAuth ya existe: {}", username);
            return existing;
        }

        Player player = new Player(
                username,
                email,
                passwordEncoder.encode(UUID.randomUUID().toString()), // Random password for OAuth users
                "FLEX",
                "LATAM",
                callbackData.provider(),
                "ALL"
        );
        // OAuth users are automatically verified
        player.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
        Player savedPlayer = playerRepository.save(player);
        log.info("Nuevo jugador registrado via OAuth ({}): {}", callbackData.provider(), savedPlayer.getUsername());
        return savedPlayer;
    }

    public String authenticate(String identifier, String password) {
        Player player = resolvePlayer(identifier);
        if (player == null) {
            return null;
        }
        if (player.isBanned()) {
            log.warn("Intento de login de usuario baneado: {}", identifier);
            return null;
        }
        if (!player.getEmailVerificationStatus().isVerified()) {
            return null;
        }
        if (!passwordEncoder.matches(password, player.getPassword())) {
            return null;
        }

        // Generate real JWT token
        String token = jwtService.generateToken(player);
        log.info("Autenticación exitosa para: {}", player.getUsername());
        return token;
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
        if (player.getEmailVerificationStatus().isVerified()) {
            return "El email ya fue verificado.";
        }

        player.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
        player.setVerificationToken(null);
        playerRepository.save(player);
        return "Email verificado exitosamente.";
    }

    public String verifyEmailWithToken(String username, String token) {
        Player player = playerRepository.findByUsername(username);
        if (player == null) {
            return "Usuario no encontrado.";
        }
        if (player.getEmailVerificationStatus().isVerified()) {
            return "El email ya fue verificado.";
        }
        if (player.getVerificationToken() == null || !player.getVerificationToken().equals(token)) {
            return "Token de verificación inválido.";
        }

        player.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
        player.setVerificationToken(null);
        playerRepository.save(player);
        return "Email verificado exitosamente.";
    }
}
