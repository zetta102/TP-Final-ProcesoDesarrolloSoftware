package com.pds.tp.domain.moderation;

import com.pds.tp.domain.entity.Report;
import com.pds.tp.infrastructure.repository.ReportRepository;
import lombok.Setter;

/**
 * Chain of Responsibility node for report moderation.
 */
@Setter
public abstract class ModerationNode {
    protected ModerationNode next;

    public abstract void handle(Report report, ReportRepository reportRepository);

    protected void passToNext(Report report, ReportRepository reportRepository) {
        if (this.next != null) {
            this.next.handle(report, reportRepository);
        }
    }
}
