package com.pds.tp.application.service;

import com.pds.tp.application.dto.PlayerData;
import com.pds.tp.config.JwtService;
import com.pds.tp.domain.entity.Player;
import com.pds.tp.domain.valueobject.EmailVerificationStatus;
import com.pds.tp.infrastructure.MockOAuthProvider;
import com.pds.tp.infrastructure.notification.EmailService;
import com.pds.tp.infrastructure.repository.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.pds.tp.support.TestFixtures.player;
import static com.pds.tp.support.TestFixtures.setField;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private MockOAuthProvider oAuthProvider;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterWithEncodedPassword() {
        PlayerData input = new PlayerData("neo", "neo@test.com", "plain", "FLEX", "LAS", "PC", "NOCHE");

        when(playerRepository.existsByUsername("neo")).thenReturn(false);
        when(playerRepository.existsByEmail("neo@test.com")).thenReturn(false);
        when(passwordEncoder.encode("plain")).thenReturn("hashed");
        when(playerRepository.save(any(Player.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Player saved = authService.register(input);

        assertEquals("neo", saved.getUsername());
        assertEquals("hashed", saved.getPassword());
    }

    @Test
    void shouldRejectWhenUsernameAlreadyExists() {
        PlayerData input = new PlayerData("neo", "neo@test.com", "plain", "FLEX", "LAS", "PC", "NOCHE");
        when(playerRepository.existsByUsername("neo")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> authService.register(input));
        verify(playerRepository, never()).save(any(Player.class));
    }

    @Test
    void shouldAuthenticateByEmailWhenVerified() {
        Player verifiedPlayer = player("neo", "neo@test.com", "LAS");
        verifiedPlayer.setEmailVerificationStatus(EmailVerificationStatus.VERIFIED);
        setField(verifiedPlayer, "password", "hashed");

        when(playerRepository.findByEmail("neo@test.com")).thenReturn(verifiedPlayer);
        when(passwordEncoder.matches("plain", "hashed")).thenReturn(true);
        when(jwtService.generateToken(verifiedPlayer)).thenReturn("mock-jwt-token");

        String token = authService.authenticate("neo@test.com", "plain");
        assertNotNull(token);
        assertEquals("mock-jwt-token", token);
    }

    @Test
    void shouldFailAuthenticationWhenEmailNotVerified() {
        Player pendingPlayer = player("neo", "neo@test.com", "LAS");
        pendingPlayer.setEmailVerificationStatus(EmailVerificationStatus.PENDING);
        setField(pendingPlayer, "password", "hashed");

        when(playerRepository.findByUsername("neo")).thenReturn(pendingPlayer);

        String token = authService.authenticate("neo", "plain");
        assertNull(token);
        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void shouldVerifyEmail() {
        Player pendingPlayer = player("neo", "neo@test.com", "LAS");
        pendingPlayer.setEmailVerificationStatus(EmailVerificationStatus.PENDING);
        setField(pendingPlayer, "password", "hashed");

        when(playerRepository.findByUsername("neo")).thenReturn(pendingPlayer);

        String result = authService.verifyEmail("neo");

        assertEquals("Email verificado exitosamente.", result);
        assertEquals(EmailVerificationStatus.VERIFIED, pendingPlayer.getEmailVerificationStatus());
        verify(playerRepository).save(pendingPlayer);
    }
}

