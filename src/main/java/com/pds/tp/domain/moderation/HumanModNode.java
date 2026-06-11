package com.pds.tp.domain.moderation;

import com.pds.tp.domain.entity.Report;
import com.pds.tp.infrastructure.repository.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HumanModNode extends ModerationNode {
    @Override
    public void handle(Report report, ReportRepository reportRepository) {
        log.info("HumanModNode: Reporte escalado a humanos. Añadiendo ticket a la cola del Dashboard Admin.");
        report.setStatus("ESCALATED_HUMAN");
        report.setResolutionDetails("Escalated to human moderator for manual review.");
        reportRepository.save(report);
    }
}

