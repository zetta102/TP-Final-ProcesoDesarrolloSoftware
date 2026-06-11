package com.pds.tp.controller;

import com.pds.tp.application.dto.SavedSearchRequest;
import com.pds.tp.domain.entity.Player;
import com.pds.tp.domain.entity.SavedSearch;
import com.pds.tp.infrastructure.repository.PlayerRepository;
import com.pds.tp.infrastructure.repository.SavedSearchRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/players")
public class PlayerController {

    private final PlayerRepository playerRepository;
    private final SavedSearchRepository savedSearchRepository;

    public PlayerController(PlayerRepository playerRepository, SavedSearchRepository savedSearchRepository) {
        this.playerRepository = playerRepository;
        this.savedSearchRepository = savedSearchRepository;
    }

    @PostMapping("/{username}/saved-searches")
    public ResponseEntity<SavedSearch> createSavedSearch(
            @PathVariable String username,
            @RequestBody SavedSearchRequest request) {
        Player player = playerRepository.findByUsername(username);
        if (player == null) {
            return ResponseEntity.notFound().build();
        }

        SavedSearch savedSearch = new SavedSearch(
                player,
                request.game(),
                request.region(),
                request.minRank(),
                request.maxRank(),
                request.maxLatency(),
                request.format()
        );

        return ResponseEntity.status(201).body(savedSearchRepository.save(savedSearch));
    }

    @GetMapping("/{username}/saved-searches")
    public ResponseEntity<List<SavedSearch>> getSavedSearches(@PathVariable String username) {
        Player player = playerRepository.findByUsername(username);
        if (player == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(savedSearchRepository.findAllByPlayer(player));
    }

    @DeleteMapping("/{username}/saved-searches/{searchId}")
    public ResponseEntity<Void> deleteSavedSearch(
            @PathVariable String username,
            @PathVariable String searchId) {
        savedSearchRepository.deleteById(java.util.UUID.fromString(searchId));
        return ResponseEntity.noContent().build();
    }
}

