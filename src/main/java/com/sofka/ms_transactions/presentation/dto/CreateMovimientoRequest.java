package com.sofka.ms_transactions.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMovimientoRequest {

    @NotNull(message = "Fecha is required")
    private LocalDate fecha;

    @NotBlank(message = "Tipo de movimiento is required")
    private String tipoMovimiento;

    @NotNull(message = "Valor is required")
    private Double valor;

    @NotNull(message = "CuentaId is required")
    private Long cuentaId;
}