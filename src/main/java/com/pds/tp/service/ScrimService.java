package com.pds.tp.service;

import com.pds.tp.entity.*;
import com.pds.tp.model.*;
import com.pds.tp.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ScrimService {
    private final ScrimRepository scrimRepository;
    private final LobbyRepository lobbyRepository;
    private final ReportRepository reportRepository;
    private final PlayerRepository playerRepository;
    private final ScrimStatisticsRepository scrimStatisticsRepository;

    public ScrimService(ScrimRepository scrimRepository, LobbyRepository lobbyRepository, ReportRepository reportRepository, PlayerRepository playerRepository, ScrimStatisticsRepository scrimStatisticsRepository) {
        this.scrimRepository = scrimRepository;
        this.lobbyRepository = lobbyRepository;
        this.reportRepository = reportRepository;
        this.playerRepository = playerRepository;
        this.scrimStatisticsRepository = scrimStatisticsRepository;
    }

    public Lobby createLobby(LobbyData lobbyData) {
        Player player = playerRepository.findByUsername(lobbyData.hostUserName());
        Lobby lobby = new Lobby(
                LocalDateTime.now(),
                lobbyData.maxPlayers(),
                lobbyData.minPlayers(),
                player.getRegion(),
                lobbyData.minRank(),
                lobbyData.maxRank(),
                lobbyData.maxPing(),
                lobbyData.gameMode(),
                lobbyData.map(),
                "Waiting",
                player,
                List.of()
        );
        return lobbyRepository.save(lobby);
    }

    public LobbyConfirmation applyToLobby(LobbyApplication lobbyApplication) {

        Lobby lobby = lobbyRepository.getReferenceById(UUID.fromString(lobbyApplication.lobbyId()));

        if (!lobby.getStatus().equals("Waiting")) {
            log.info("Lobby is not in waiting status");
            return new LobbyConfirmation(lobbyApplication.username(), lobbyApplication.lobbyId(), "Rejected", "Lobby's already full.");
        }

        Player player = playerRepository.findByUsername(lobbyApplication.username());

        if (lobby.getPlayers().contains(player)) {
            log.info("Player already in lobby");
            return new LobbyConfirmation(lobbyApplication.username(), lobbyApplication.lobbyId(), "Rejected", "Player already in lobby.");
        }

        lobby.getPlayers().add(player);

        if (lobby.getPlayers().size() == lobby.getMaxPlayers()) {
            log.info("Lobby {} is full. Starting the scrim.", lobby.getId());
            lobby.setStatus("Started");
        }

        lobbyRepository.save(lobby);

        return new LobbyConfirmation(player.getId().toString(), lobby.getId().toString(), "Confirmed", "Joined lobby successfully.");

    }

    @Scheduled(fixedRate = 60, timeUnit = TimeUnit.SECONDS) // Check every minute
    public void cleanupLobbies() {
        List<Lobby> lobbies = lobbyRepository.findAllByStatusEquals("Waiting");
        for (Lobby lobby : lobbies) {
            if (ChronoUnit.MINUTES.between(lobby.getScheduledTime(), LocalDateTime.now()) <= 30 &&
                    lobby.getPlayers().size() >= lobby.getMinPlayers()) {
                log.info("Lobby {} has enough players. Starting the scrim.", lobby.getId());
                lobby.setStatus("Started");
                lobbyRepository.save(lobby);
                Scrim scrim = scrimRepository.save(new Scrim(lobby, lobby.getGameMode(), lobby.getMap(), "Started"));
                List<Player> teamRed = lobby.getPlayers().subList(0, lobby.getPlayers().size() / 2);
                List<Player> teamBlue = lobby.getPlayers().subList(lobby.getPlayers().size() / 2, lobby.getPlayers().size());
                ScrimStatistics scrimStatistics = new ScrimStatistics(scrim, teamRed, teamBlue);
                scrimStatisticsRepository.save(scrimStatistics);
            } else {
                log.info("Lobby {} did not fulfill the required amount of players after 30 minutes. Now cancelling it.", lobby.getId());
                cancelLobbyById(lobby.getId());
            }
        }
    }

    public Scrim startScrim(ScrimData scrimData) {
        Lobby lobby = lobbyRepository.getReferenceById(UUID.fromString(scrimData.lobbyId()));
        if (lobby.getPlayers().size() < lobby.getMinPlayers()) {
            throw new IllegalStateException("Not enough players to start the scrim. Wait and try again.");
        }
        Scrim scrim = scrimRepository.save(new Scrim(lobby, lobby.getGameMode(), lobby.getMap(), "Started"));
        List<Player> teamRed = lobby.getPlayers().subList(0, lobby.getPlayers().size() / 2);
        List<Player> teamBlue = lobby.getPlayers().subList(lobby.getPlayers().size() / 2, lobby.getPlayers().size());
        ScrimStatistics scrimStatistics = new ScrimStatistics(scrim, teamRed, teamBlue);
        scrimStatisticsRepository.save(scrimStatistics);
        return scrim;
    }

    public List<Lobby> findActiveLobbiesByRegionAndRank(FindLobbyData findLobbyData) {
        return lobbyRepository.findAllByRegionAndMinRankLessThanEqualAndMaxRankGreaterThanEqualAndMaxPingLessThanEqualAndStatusEquals(
                        findLobbyData.region(),
                        findLobbyData.minRank(),
                        findLobbyData.maxRank(),
                        findLobbyData.maxPing(),
                        "Waiting"
                )
                .stream()
                //.filter(lobby -> lobby.getScheduledTime());
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
        if (scrim.getStatus().equals("Started")) {
            scrim.setStatus("Finished");
            scrimRepository.save(scrim);
            ScrimStatistics scrimStatistics = scrimStatisticsRepository.findByScrimId(scrim);
            scrimStatistics.setEndTime(LocalDateTime.now());
            scrimStatistics.setStatus("Finished");
            scrimStatisticsRepository.save(scrimStatistics);
            return "Scrim " + scrim.getId() + " finished successfully.";
        } else {
            throw new IllegalStateException("Only scrims with status 'Started' can be finished.");
        }
    }

    public ReportConfirmation reportPlayer(ReportApplication reportApplication) {
        Player reportingPlayer = playerRepository.findByUsername(reportApplication.reportingPlayerUsername());
        Player reportedPlayer = playerRepository.findByUsername(reportApplication.reportedPlayerUsername());
        Scrim scrim = scrimRepository.getReferenceById(UUID.fromString(reportApplication.lobbyId()));

        if (reportingPlayer == null || reportedPlayer == null) {
            throw new IllegalArgumentException("One or both players not found.");
        }

        if (reportedPlayer.equals(reportingPlayer)) {
            throw new IllegalArgumentException("You cannot report yourself.");
        }

        if (!scrim.getStatus().equals("Finished")) {
            throw new IllegalStateException("You cannot report players from a non-finished scrim.");
        }

        Report report = reportRepository.save(new Report(scrim, reportingPlayer, reportedPlayer, reportApplication.reason(), ""));

        return new ReportConfirmation(report.getId(), report.getReportedPlayer().getUsername(), report.getScrimId().toString(), report.getReportingPlayer().getUsername(), report.getStatus());
    }

    public ScrimStatistics getStatistics(UUID scrimId) {
        Scrim scrim = scrimRepository.getReferenceById(scrimId);
        return scrimStatisticsRepository.findByScrimId(scrim);
    }
}
