package com.pds.tp.application.dto;

public record FindLobbyData(
        String juego,
        String region,
        String rangoMin,
        String rangoMax,
        String fecha,
        Integer latenciaMax
) {
}


