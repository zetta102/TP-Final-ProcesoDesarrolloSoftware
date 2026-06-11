package com.pds.tp.domain.moderation;

import com.pds.tp.domain.entity.Report;
import com.pds.tp.domain.valueobject.ReportStatus;
import com.pds.tp.infrastructure.repository.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HumanModNode extends ModerationNode {
    @Override
    public void handle(Report report, ReportRepository reportRepository) {
        log.info("HumanModNode: Reporte escalado a humanos. Añadiendo ticket a la cola del Dashboard Admin.");
        report.setStatus(ReportStatus.ESCALADO_HUMANO);
        report.setResolutionDetails("Escalado a moderador humano para revisión manual.");
        reportRepository.save(report);
    }
}
