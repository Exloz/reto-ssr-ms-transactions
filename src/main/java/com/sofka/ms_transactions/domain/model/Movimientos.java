package com.sofka.ms_transactions.domain.model;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("movimientos")
public class Movimientos {

    @Id
    @Column("movimiento_id")
    private Long movimientoId;

    @NotNull(message = "Fecha is required")
    @Column("fecha")
    private LocalDate fecha;

    @NotBlank(message = "Tipo de movimiento is required")
    @Column("tipo_movimiento")
    private String tipoMovimiento;

    @NotNull(message = "Valor is required")
    @Column("valor")
    private Double valor;

    @NotNull(message = "Saldo is required")
    @Column("saldo")
    private Double saldo;

    @NotNull(message = "CuentaId is required")
    @Column("cuenta_id")
    private Long cuentaId;
}
