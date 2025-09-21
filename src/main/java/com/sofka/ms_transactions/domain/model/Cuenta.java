package com.sofka.ms_transactions.domain.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cuenta {

    private Long cuentaId;

    @NotBlank(message = "Número de cuenta is required")
    private String numeroCuenta;

    @NotBlank(message = "Tipo de cuenta is required")
    private String tipoCuenta;

    @NotNull(message = "Saldo inicial is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Saldo inicial must be positive")
    private Double saldoInicial;

    @NotNull(message = "Estado is required")
    private Boolean estado;
}