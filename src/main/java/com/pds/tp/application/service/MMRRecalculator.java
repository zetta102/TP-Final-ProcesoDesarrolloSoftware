package com.pds.tp.application.service;

import com.pds.tp.domain.entity.Player;
import com.pds.tp.domain.entity.PlayerScrimStats;
import com.pds.tp.domain.shared.RankScale;
import com.pds.tp.infrastructure.repository.PlayerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service responsible for recalculating player MMR/rank after a scrim finishes.
 * Updates player statistics (games played, wins, losses, KDA) and promotes/demotes visible rank.
 */
@Slf4j
@Service
public class MMRRecalculator {

    private static final int MMR_WIN_GAIN = 25;
    private static final int MMR_LOSS_PENALTY = 20;
    private static final int RANK_THRESHOLD = 100;

    private final PlayerRepository playerRepository;

    public MMRRecalculator(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * Recalculates MMR for all players in the scrim based on their individual stats and win/loss.
     *
     * @param playerStats list of per-player stats for the scrim
     * @param winningTeam "RED" or "BLUE"
     * @param redTeam     players on the red team
     * @param blueTeam    players on the blue team
     */
    public void recalculate(List<PlayerScrimStats> playerStats, String winningTeam,
                            List<Player> redTeam, List<Player> blueTeam) {
        for (PlayerScrimStats stats : playerStats) {
            Player player = stats.getPlayer();
            boolean won = isWinner(player, winningTeam, redTeam, blueTeam);

            // Update game counters
            player.setGamesPlayed(player.getGamesPlayed() + 1);
            if (won) {
                player.setWins(player.getWins() + 1);
            } else {
                player.setLosses(player.getLosses() + 1);
            }

            // Update KDA (running average)
            double newKda = stats.getKdaRatio();
            double currentKda = player.getKda();
            int games = player.getGamesPlayed();
            player.setKda(((currentKda * (games - 1)) + newKda) / games);

            // Update MMR rank points
            int mmrChange = won ? MMR_WIN_GAIN : -MMR_LOSS_PENALTY;
            // Bonus for MVP
            if (stats.isMvp()) {
                mmrChange += 10;
            }
            int newRank = Math.max(0, player.getRank() + mmrChange);
            player.setRank(newRank);

            // Promote/demote visible rank based on thresholds
            String newVisibleRank = RankScale.fromValue(newRank / RANK_THRESHOLD);
            if (!newVisibleRank.equals(player.getVisibleRank())) {
                log.info("Jugador {} cambió de rango: {} → {}", player.getUsername(), player.getVisibleRank(), newVisibleRank);
                player.setVisibleRank(newVisibleRank);
            }

            playerRepository.save(player);
            log.debug("MMR actualizado para {}: rank={}, visibleRank={}, kda={}",
                    player.getUsername(), newRank, newVisibleRank, player.getKda());
        }
    }

    private boolean isWinner(Player player, String winningTeam, List<Player> redTeam, List<Player> blueTeam) {
        if ("RED".equalsIgnoreCase(winningTeam)) {
            return redTeam.contains(player);
        } else if ("BLUE".equalsIgnoreCase(winningTeam)) {
            return blueTeam.contains(player);
        }
        return false;
    }
}


