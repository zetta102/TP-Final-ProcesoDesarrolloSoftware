package com.pds.tp.model;

import com.pds.tp.entity.Report;
import lombok.Setter;

@Setter
public abstract class ModerationNode {
    protected ModerationNode next;

    public abstract void handle(Report report);

    protected void passToNext(Report report) {
        if (this.next != null) {
            this.next.handle(report);
        }
    }
}