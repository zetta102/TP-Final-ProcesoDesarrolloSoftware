package com.pds.tp.domain.strategy;

import com.pds.tp.domain.entity.Lobby;
import com.pds.tp.domain.entity.Player;
import com.pds.tp.domain.shared.RankScale;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

/**
 * Matchmaking strategy that selects players based on history/compatibility:
 * - Excludes banned players
 * - Excludes players already in the lobby
 * - Penalizes players with high strikes (abandonment)
 * - Prioritizes by win rate, KDA, and low strikes
 * - Filters by region and rank range
 */
@Component
public class ByHistoryStrategy implements MatchmakingStrategy {
    @Override
    public List<Player> seleccionar(List<Player> candidatos, Lobby lobby) {
        int minRank = RankScale.toValue(lobby.getMinRank());
        int maxRank = RankScale.toValue(lobby.getMaxRank());

        return candidatos.stream()
                // Exclude banned players
                .filter(player -> !player.isBanned())
                // Exclude players already in the lobby
                .filter(player -> !lobby.getPlayers().contains(player))
                // Filter by region
                .filter(player -> player.getRegion().equalsIgnoreCase(lobby.getRegion()))
                // Filter by rank range
                .filter(player -> {
                    int rank = RankScale.toValue(player.getVisibleRank());
                    return rank >= minRank && rank <= maxRank;
                })
                // Sort by composite score: win rate * KDA bonus - strike penalty
                .sorted(Comparator.comparingDouble(this::computeCompatibilityScore).reversed())
                .limit(Math.max(0, lobby.getMaxPlayers() - lobby.getPlayers().size()))
                .toList();
    }

    private double computeCompatibilityScore(Player player) {
        double winRate = player.getGamesPlayed() > 0
                ? (double) player.getWins() / player.getGamesPlayed()
                : 0.5;
        double kdaBonus = Math.min(player.getKda(), 5.0) / 5.0; // normalize KDA to [0,1]
        double strikePenalty = player.getStrikes() * 0.2;

        return (winRate * 0.6) + (kdaBonus * 0.3) - strikePenalty;
    }
}
