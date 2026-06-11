package com.pds.tp.domain.moderation;

import com.pds.tp.domain.entity.Report;
import com.pds.tp.domain.valueobject.ReportStatus;
import com.pds.tp.infrastructure.repository.ReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class BotAnalyzerNode extends ModerationNode {

    private final List<String> toxicKeywords = List.of("insulto", "troll", "flame", "toxic");

    @Override
    public void handle(Report report, ReportRepository reportRepository) {
        log.info("BotAnalyzerNode: Ejecutando NLP sobre el reporte...");

        boolean isToxic = toxicKeywords.stream().anyMatch(keyword ->
                report.getDescription() != null && report.getDescription().toLowerCase().contains(keyword));

        if (isToxic) {
            log.info("BotAnalyzerNode: Toxicidad detectada en el chat. Aplicando restricción de chat.");
            report.setStatus(ReportStatus.RESUELTO_BOT);
            report.setResolvedAt(LocalDate.now().toString());
            report.setResolutionDetails("Resuelto por bot: lenguaje tóxico detectado en la descripción.");
            reportRepository.save(report);
        } else {
            log.info("BotAnalyzerNode: Contexto complejo. Escalando a moderación humana...");
            passToNext(report, reportRepository);
        }
    }
}
