package com.sofka.ms_transactions.domain.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movimientos {

    private Long movimientoId;

    @NotNull(message = "Fecha is required")
    private LocalDate fecha;

    @NotBlank(message = "Tipo de movimiento is required")
    private String tipoMovimiento;

    @NotNull(message = "Valor is required")
    private Double valor;

    @NotNull(message = "Saldo is required")
    private Double saldo;

    private Long cuentaId;
}