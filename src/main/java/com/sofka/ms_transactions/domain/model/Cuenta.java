package com.sofka.ms_transactions.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("cuentas")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Cuenta {

    @Id
    @Column("cuenta_id")
    private Long cuentaId;

    @NotBlank(message = "Número de cuenta is required")
    @Column("numero_cuenta")
    private String numeroCuenta;

    @NotBlank(message = "Tipo de cuenta is required")
    @Column("tipo_cuenta")
    private String tipoCuenta;

    @NotNull(message = "Saldo inicial is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Saldo inicial must be non-negative")
    @Column("saldo_inicial")
    private Double saldoInicial;

    @Column("saldo_actual")
    private Double saldoActual;

    @NotNull(message = "Estado is required")
    @Column("estado")
    private Boolean estado;

    @NotNull(message = "ClienteId is required")
    @Column("cliente_id")
    private Long clienteId;
}
