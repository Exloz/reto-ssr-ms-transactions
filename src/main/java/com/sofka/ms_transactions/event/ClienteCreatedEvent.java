package com.sofka.ms_transactions.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClienteCreatedEvent {
    private Long clienteId;
    private String nombre;
    private String identificacion;
}