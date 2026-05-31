package com.pds.tp.model;


public interface ScrimCommand {
    void execute(ScrimContext ctx);

    void undo(ScrimContext ctx);
}