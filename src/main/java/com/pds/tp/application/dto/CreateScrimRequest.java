package com.pds.tp.application.dto;

public record CreateScrimRequest(
        String juego,
        String formato,
        int cantidadJugadoresPorLado,
        int cantidadTotalJugadores,
        String region,
        String rangoMin,
        String rangoMax,
        int latenciaMax,
        String fecha,
        String duracion,
        String modalidad,
        String mapa,
        String hostUserName
) {
}

