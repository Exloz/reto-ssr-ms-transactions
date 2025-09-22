package com.sofka.ms_transactions.presentation.dto;

import java.util.List;

public record AccountStatementResponse(Long clienteId,
                                       Long cuentaId,
                                       String numeroCuenta,
                                       String tipoCuenta,
                                       Double saldoInicial,
                                       Double saldoActual,
                                       List<MovementDetailResponse> movimientos) {
}
