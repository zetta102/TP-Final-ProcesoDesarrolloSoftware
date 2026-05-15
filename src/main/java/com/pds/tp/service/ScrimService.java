package com.pds.tp.service;

import com.pds.tp.entity.Lobby;
import com.pds.tp.entity.Player;
import com.pds.tp.entity.Scrim;
import com.pds.tp.model.FindLobbyData;
import com.pds.tp.model.LobbyData;
import com.pds.tp.model.ScrimData;
import com.pds.tp.repository.LobbyRepository;
import com.pds.tp.repository.PlayerRepository;
import com.pds.tp.repository.ReportRepository;
import com.pds.tp.repository.ScrimRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ScrimService {
    private final ScrimRepository scrimRepository;
    private final LobbyRepository lobbyRepository;
    private final ReportRepository reportRepository;
    private final PlayerRepository playerRepository;

    public ScrimService(ScrimRepository scrimRepository, LobbyRepository lobbyRepository, ReportRepository reportRepository, PlayerRepository playerRepository) {
        this.scrimRepository = scrimRepository;
        this.lobbyRepository = lobbyRepository;
        this.reportRepository = reportRepository;
        this.playerRepository = playerRepository;
    }

    public Lobby createLobby(LobbyData lobbyData) {
        Player player = playerRepository.findByUsername(lobbyData.hostUserName());
        Lobby lobby = new Lobby(
                LocalDateTime.now().toString(),
                lobbyData.maxPlayers(),
                lobbyData.minPlayers(),
                player.getRegion(),
                lobbyData.minRank(),
                lobbyData.maxRank(),
                lobbyData.maxPing(),
                lobbyData.gameMode(),
                lobbyData.map(),
                "Started",
                player,
                List.of()
        );
        return lobbyRepository.save(lobby);
    }

    public Scrim startScrim(ScrimData scrimData) {
        Lobby lobby = lobbyRepository.getReferenceById(UUID.fromString(scrimData.lobbyId()));
        if (lobby.getPlayers().size() < lobby.getMinPlayers()) {
            throw new IllegalStateException("Not enough players to start the scrim. Wait and try again.");
        }
        Scrim scrim = new Scrim();
        return scrimRepository.save(scrim);
    }

    public List<Lobby> findActiveLobbiesByRegionAndRank(FindLobbyData findLobbyData) {
        return lobbyRepository.findAllByRegionAndMinRankLessThanEqualAndMaxRankGreaterThanEqual(
                findLobbyData.region(),
                findLobbyData.minRank(),
                findLobbyData.maxRank()
        )
                .stream()
                //.filter(lobby -> lobby.getScheduledTime());
                .filter(lobby -> lobby.getMaxPing() <= Integer.parseInt(findLobbyData.latenciaMax()))
                .toList();
    }

    public String cancelLobbyById(UUID lobbyId) {
        Lobby lobby = lobbyRepository.getReferenceById(lobbyId);
        boolean b = lobby.getStatus().equals("Started");
        if (b) {
            lobby.setStatus("Canceled");
            lobbyRepository.save(lobby);
            return "Lobby " + lobby.getId() + " canceled successfully.";
        } else {
            throw new IllegalStateException("Only lobbies with status 'Started' can be canceled.");
        }
    }

    public String finishScrimById(UUID scrimId) {
        Scrim scrim = scrimRepository.getReferenceById(scrimId);
        boolean b = scrim.getStatus().equals("Started");
        if (b) {
            scrim.setStatus("Finished");
            scrimRepository.save(scrim);
            return "Scrim " + scrim.getId() + " finished successfully.";
        } else {
            throw new IllegalStateException("Only scrims with status 'Started' can be finished.");
        }
    }
}
