package com.pds.tp.domain.state;

public class FinishedState extends AbstractTerminalState {
    @Override
    protected String defaultTerminalMessage() {
        return "El scrim ya fue finalizado.";
    }

    @Override
    protected String finalizeMessage() {
        return "El scrim ya fue finalizado.";
    }

    @Override
    public String getStatusName() {
        return "Finalizado";
    }
}
