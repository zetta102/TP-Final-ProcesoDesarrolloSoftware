package com.pds.tp.service;


import com.pds.tp.entity.Player;
import com.pds.tp.entity.Report;
import com.pds.tp.entity.Scrim;
import com.pds.tp.model.*;
import com.pds.tp.repository.PlayerRepository;
import com.pds.tp.repository.ReportRepository;
import com.pds.tp.repository.ScrimRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReportService {
    private final ReportRepository reportRepository;
    private final PlayerRepository playerRepository;
    private final ScrimRepository scrimRepository;

    // Chain Nodes
    private final AutoResolverNode autoResolver;
    private final BotAnalyzerNode botAnalyzer;
    private final HumanModNode humanMod;

    public ReportService(ReportRepository reportRepository, PlayerRepository playerRepository,
                         ScrimRepository scrimRepository, AutoResolverNode autoResolver,
                         BotAnalyzerNode botAnalyzer, HumanModNode humanMod) {
        this.reportRepository = reportRepository;
        this.playerRepository = playerRepository;
        this.scrimRepository = scrimRepository;
        this.autoResolver = autoResolver;
        this.botAnalyzer = botAnalyzer;
        this.humanMod = humanMod;
    }

    @PostConstruct
    public void initChain() {
        // Enlazar la cadena de responsabilidad
        autoResolver.setNext(botAnalyzer);
        botAnalyzer.setNext(humanMod);
    }

    public ReportConfirmation processReport(ReportApplication reportApp) {
        Player reportingPlayer = playerRepository.findByUsername(reportApp.reportingPlayerUsername());
        Player reportedPlayer = playerRepository.findByUsername(reportApp.reportedPlayerUsername());
        Scrim scrim = scrimRepository.getReferenceById(UUID.fromString(reportApp.lobbyId()));

        if (!scrim.getStatus().equals("Finalizado")) {
            throw new IllegalStateException("You cannot report players from a non-finished scrim.");
        }

        Report report = new Report(scrim, reportingPlayer, reportedPlayer, reportApp.reason(), "Context Description Placeholder");
        report = reportRepository.save(report);

        // Disparar el pipeline de moderación
        autoResolver.handle(report);

        return new ReportConfirmation(report.getId(), report.getReportedPlayer().getUsername(),
                report.getScrimId().toString(), report.getReportingPlayer().getUsername(), report.getStatus());
    }
}