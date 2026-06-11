package com.pds.tp.infrastructure.repository;

import com.pds.tp.domain.entity.Player;
import com.pds.tp.domain.entity.SavedSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SavedSearchRepository extends JpaRepository<SavedSearch, UUID> {
    List<SavedSearch> findAllByPlayer(Player player);

    List<SavedSearch> findAll();
}

