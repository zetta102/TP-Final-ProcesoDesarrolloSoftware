package com.pds.tp.application.command;

import com.pds.tp.domain.state.ScrimContext;

public interface ScrimCommand {
    void execute(ScrimContext ctx);

    void undo(ScrimContext ctx);
}

