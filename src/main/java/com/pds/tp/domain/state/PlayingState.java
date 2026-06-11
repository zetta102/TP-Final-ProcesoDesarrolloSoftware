package com.pds.tp.domain.state;

public class PlayingState extends AbstractOperationalState {
    @Override
    protected String postularRejectedMessage() {
        return "El scrim está en curso.";
    }

    @Override
    protected String confirmarRejectedMessage() {
        return "El scrim está en curso.";
    }

    @Override
    protected String iniciarRejectedMessage() {
        return "El scrim ya fue iniciado.";
    }

    @Override
    public void finalizar(ScrimContext ctx) {
        ctx.setState(new FinishedState());
    }

    @Override
    protected String cancelarRejectedMessage() {
        return "No se puede cancelar un scrim en curso; debe finalizarse.";
    }

    @Override
    public String getStatusName() {
        return "EnJuego";
    }
}
