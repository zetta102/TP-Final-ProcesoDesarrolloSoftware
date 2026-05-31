package com.pds.tp.domain.state;

public class CanceledState extends AbstractTerminalState {
    @Override
    protected String cancelMessage() {
        return "Ya cancelado.";
    }

    @Override
    public String getStatusName() {
        return "Cancelado";
    }
}

