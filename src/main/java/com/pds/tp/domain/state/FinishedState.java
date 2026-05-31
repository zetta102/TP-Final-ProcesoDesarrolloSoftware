package com.pds.tp.domain.state;

public class FinishedState extends AbstractTerminalState {
    @Override
    protected String finalizeMessage() {
        return "Ya finalizado.";
    }

    @Override
    public String getStatusName() {
        return "Finalizado";
    }
}

