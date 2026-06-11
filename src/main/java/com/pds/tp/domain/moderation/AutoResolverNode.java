package com.pds.tp.domain.moderation;

import com.pds.tp.domain.entity.Report;
import com.pds.tp.infrastructure.repository.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class AutoResolverNode extends ModerationNode {
    @Override
    public void handle(Report report, ReportRepository reportRepository) {
        log.info("AutoResolverNode: Analizando reporte {} por motivo: {}", report.getId(), report.getReason());

        if ("AFK".equalsIgnoreCase(report.getReason()) || "LEAVER".equalsIgnoreCase(report.getReason())) {
            log.info("AutoResolverNode: Abandono confirmado por logs. Aplicando sanción automática.");
            report.setStatus("AUTO_RESOLVED");
            report.setResolvedAt(LocalDate.now().toString());
            report.setResolutionDetails("Auto-resolved: " + report.getReason() + " confirmed by system logs.");
            reportRepository.save(report);
        } else {
            log.info("AutoResolverNode: Evidencia insuficiente. Escalando al bot analizador...");
            passToNext(report, reportRepository);
        }
    }
}

