package com.sofka.ms_transactions.presentation.dto;

import java.time.LocalDate;

public record MovementDetailResponse(LocalDate fecha,
                                     String tipoMovimiento,
                                     Double valor,
                                     Double saldoDisponible) {
}
