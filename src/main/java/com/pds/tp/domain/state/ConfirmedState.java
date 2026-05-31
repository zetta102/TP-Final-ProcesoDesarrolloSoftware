package com.pds.tp.domain.state;

public class ConfirmedState extends AbstractOperationalState {
    @Override
    protected String postularRejectedMessage() {
        return "Cupo completo y confirmado.";
    }

    @Override
    protected String confirmarRejectedMessage() {
        return "Ya están todos confirmados.";
    }

    @Override
    public void iniciar(ScrimContext ctx) {
        ctx.setState(new PlayingState());
    }

    @Override
    protected String finalizarRejectedMessage() {
        return "Debe iniciar antes de finalizar.";
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

