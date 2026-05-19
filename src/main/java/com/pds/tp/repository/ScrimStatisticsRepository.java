package com.pds.tp.repository;

import com.pds.tp.entity.Scrim;
import com.pds.tp.entity.ScrimStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScrimStatisticsRepository extends JpaRepository<ScrimStatistics, UUID> {
    ScrimStatistics findByScrimId(Scrim scrim);
}
