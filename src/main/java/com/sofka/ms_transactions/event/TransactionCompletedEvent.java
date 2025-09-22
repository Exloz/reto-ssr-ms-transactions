package com.sofka.ms_transactions.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCompletedEvent {
    private Long cuentaId;
    private Double valor;
    private String tipoMovimiento;
}