package com.pds.tp.domain.state;

public class ConfirmedState extends AbstractOperationalState {
    @Override
    protected String postularRejectedMessage() {
        return "El lobby está completo y ya fue confirmado.";
    }

    @Override
    protected String confirmarRejectedMessage() {
        return "Todos los jugadores ya confirmaron.";
    }

    @Override
    public void iniciar(ScrimContext ctx) {
        ctx.setState(new PlayingState());
    }

    @Override
    protected String finalizarRejectedMessage() {
        return "El scrim debe iniciar antes de poder finalizarlo.";
    }

    @Override
    public void cancelar(ScrimContext ctx) {
        ctx.setState(new CanceledState());
    }

    @Override
    public String getStatusName() {
        return "Confirmado";
    }
}
