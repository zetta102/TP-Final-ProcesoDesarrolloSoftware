package com.pds.tp.domain.moderation;

import com.pds.tp.domain.entity.Report;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AutoResolverNode extends ModerationNode {
    @Override
    public void handle(Report report) {
        log.info("AutoResolverNode: Analizando reporte {} por motivo: {}", report.getId(), report.getReason());

        // Simulación: Si el motivo es AFK, el sistema lo auto-resuelve verificando logs del servidor
        if ("AFK".equalsIgnoreCase(report.getReason()) || "LEAVER".equalsIgnoreCase(report.getReason())) {
            log.info("AutoResolverNode: Abandono confirmado por logs. Aplicando sanción automática.");
            // En la vida real: db.updateStatus("RESUELTO_AUTO");
        } else {
            log.info("AutoResolverNode: Evidencia insuficiente. Escalando al bot analizador...");
            passToNext(report);
        }
    }
}

