package com.pds.tp.application.service;

import com.pds.tp.application.dto.LobbyApplication;
import com.pds.tp.application.dto.LobbyConfirmation;
import com.pds.tp.application.dto.ScrimData;
import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import com.pds.tp.domain.entity.Scrim;
import com.pds.tp.domain.entity.ScrimStatistics;
import com.pds.tp.domain.state.ScrimStateResolver;
import com.pds.tp.domain.strategy.ByMMRStrategy;
import com.pds.tp.infrastructure.repository.LobbyRepository;
import com.pds.tp.infrastructure.repository.PlayerRepository;
import com.pds.tp.infrastructure.repository.ScrimRepository;
import com.pds.tp.infrastructure.repository.ScrimStatisticsRepository;
import com.pds.tp.infrastructure.repository.WaitlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScrimServiceFlowIntegrationTest {

    @Mock
    private ScrimRepository scrimRepository;

    @Mock
    private LobbyRepository lobbyRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private ScrimStatisticsRepository scrimStatisticsRepository;

    @Mock
    private WaitlistRepository waitlistRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private ScrimService scrimService;

    @BeforeEach
    void setUp() {
        scrimService = new ScrimService(
                scrimRepository,
                lobbyRepository,
                playerRepository,
                scrimStatisticsRepository,
                waitlistRepository,
                eventPublisher,
                new ByMMRStrategy(),
                new ScrimStateResolver()
        );
    }

    @Test
    void shouldExecuteCoreFlowApplyConfirmStartAndFinish() {
        Player host = new Player("host", "host@test.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        Player candidate = new Player("candidate", "candidate@test.com", "pwd", "FLEX", "LAS", "PC", "NOCHE");
        setId(host, UUID.randomUUID());
        setId(candidate, UUID.randomUUID());
        setVisibleRank(host, "PLATA");
        setVisibleRank(candidate, "PLATA");

        Lobby lobby = new Lobby(
                LocalDateTime.now().plusHours(1),
                2,
                1,
                "LAS",
                "BRONCE",
                "DIAMANTE",
                80,
                "VALORANT",
                "HAVEN",
                "Buscando",
                host,
                new ArrayList<>() {{
                    add(host);
                }},
                new HashSet<>()
        );
        UUID lobbyId = UUID.randomUUID();
        setId(lobby, lobbyId);

        when(lobbyRepository.getReferenceById(lobbyId)).thenReturn(lobby);
        when(lobbyRepository.save(any(Lobby.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(playerRepository.findByUsername("candidate")).thenReturn(candidate);
        when(playerRepository.findByUsername("host")).thenReturn(host);

        LobbyConfirmation applyResult = scrimService.applyToLobby(new LobbyApplication("candidate", lobbyId.toString(), "FLEX"));

        assertEquals("Confirmed", applyResult.status());
        assertEquals("LobbyArmado", lobby.getStatus());
        assertEquals(2, lobby.getPlayers().size());

        String confirmHost = scrimService.confirmarParticipacion(lobbyId, "host");
        String confirmCandidate = scrimService.confirmarParticipacion(lobbyId, "candidate");

        assertEquals("Jugador confirmado con éxito.", confirmHost);
        assertEquals("Jugador confirmado con éxito.", confirmCandidate);
        assertEquals("Confirmado", lobby.getStatus());

        when(scrimRepository.findByLobbyId(lobby)).thenReturn(Optional.empty());
        when(scrimRepository.save(any(Scrim.class))).thenAnswer(invocation -> {
            Scrim scrim = invocation.getArgument(0);
            if (scrim.getId() == null) {
                setId(scrim, UUID.randomUUID());
            }
            return scrim;
        });
        when(scrimStatisticsRepository.save(any(ScrimStatistics.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Scrim scrim = scrimService.startScrim(new ScrimData(lobbyId.toString()));

        assertEquals("EnJuego", lobby.getStatus());
        assertEquals("EnJuego", scrim.getStatus());

        ScrimStatistics stats = new ScrimStatistics(
                scrim,
                new ArrayList<>(lobby.getPlayers().subList(0, 1)),
                new ArrayList<>(lobby.getPlayers().subList(1, 2))
        );

        when(scrimRepository.getReferenceById(scrim.getId())).thenReturn(scrim);
        when(scrimStatisticsRepository.findByScrimId(scrim)).thenReturn(stats);

        String finishResult = scrimService.finishScrimById(scrim.getId());

        assertTrue(finishResult.contains("Scrim finalizada"));
        assertEquals("Finalizado", lobby.getStatus());
        assertEquals("Finalizado", scrim.getStatus());
    }

    private void setId(Player player, UUID id) {
        try {
            var field = Player.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(player, id);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void setId(Lobby lobby, UUID id) {
        try {
            var field = Lobby.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(lobby, id);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void setId(Scrim scrim, UUID id) {
        try {
            var field = Scrim.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(scrim, id);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void setVisibleRank(Player player, String rank) {
        try {
            var field = Player.class.getDeclaredField("visibleRank");
            field.setAccessible(true);
            field.set(player, rank);
        } catch (ReflectiveOperationException ex) {
            throw new IllegalStateException(ex);
        }
    }
}


