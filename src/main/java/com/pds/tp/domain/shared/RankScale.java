package com.pds.tp.domain.shared;

import java.util.Map;

public final class RankScale {
    private static final Map<String, Integer> RANK_VALUES = Map.of(
            "HIERRO", 1,
            "BRONCE", 2,
            "PLATA", 3,
            "ORO", 4,
            "PLATINO", 5,
            "DIAMANTE", 6,
            "RADIANTE", 7
    );

    private static final String[] RANK_NAMES = {
            "HIERRO", "HIERRO", "BRONCE", "PLATA", "ORO", "PLATINO", "DIAMANTE", "RADIANTE"
    };

    private RankScale() {
    }

    public static int toValue(String rank) {
        if (rank == null || rank.isBlank()) {
            return 0;
        }
        return RANK_VALUES.getOrDefault(rank.trim().toUpperCase(), 0);
    }

    /**
     * Converts an MMR tier index (0-7) to the corresponding rank name.
     * Values beyond the scale are clamped to the highest rank.
     */
    public static String fromValue(int tier) {
        if (tier < 0) return RANK_NAMES[0];
        if (tier >= RANK_NAMES.length) return RANK_NAMES[RANK_NAMES.length - 1];
        return RANK_NAMES[tier];
    }
}

