package com.pds.tp.model;

import com.pds.tp.entity.Report;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HumanModNode extends ModerationNode {
    @Override
    public void handle(Report report) {
        log.info("HumanModNode: Reporte escalado a humanos. Añadiendo ticket a la cola del Dashboard Admin.");
        // En la vida real: db.updateStatus("ESCALADO_HUMANO");
    }
}