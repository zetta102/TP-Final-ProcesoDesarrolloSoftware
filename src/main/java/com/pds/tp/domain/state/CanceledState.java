package com.pds.tp.domain.state;

public class CanceledState extends AbstractTerminalState {
    @Override
    protected String defaultTerminalMessage() {
        return "El scrim fue cancelado.";
    }

    @Override
    protected String cancelMessage() {
        return "El scrim ya fue cancelado.";
    }

    @Override
    public String getStatusName() {
        return "Cancelado";
    }
}
