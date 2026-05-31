package com.pds.tp.domain.state;

public class PlayingState extends AbstractOperationalState {
    @Override
    protected String postularRejectedMessage() {
        return "En juego.";
    }

    @Override
    protected String confirmarRejectedMessage() {
        return "En juego.";
    }

    @Override
    protected String iniciarRejectedMessage() {
        return "Ya está en juego.";
    }

    @Override
    public void finalizar(ScrimContext ctx) {
        ctx.setState(new FinishedState());
    }

    @Override
    protected String cancelarRejectedMessage() {
        return "No se puede cancelar una vez en juego, debe finalizarse.";
    }

    @Override
    public String getStatusName() {
        return "EnJuego";
    }
}

