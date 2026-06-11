package com.pds.tp.infrastructure.repository;

import com.pds.tp.domain.entity.PlayerScrimStats;
import com.pds.tp.domain.entity.ScrimStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PlayerScrimStatsRepository extends JpaRepository<PlayerScrimStats, UUID> {
    List<PlayerScrimStats> findAllByScrimStatistics(ScrimStatistics scrimStatistics);
}

