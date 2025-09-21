package com.sofka.ms_transactions.infrastructure.messaging;

import com.sofka.ms_transactions.domain.model.Cuenta;
import com.sofka.ms_transactions.event.ClienteCreatedEvent;
import com.sofka.ms_transactions.application.usecase.CreateCuentaUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumer {

    private final CreateCuentaUseCase createCuentaUseCase;

    @Bean
    public Consumer<ClienteCreatedEvent> clienteCreatedConsumer() {
        return event -> {
            log.info("Received ClienteCreatedEvent: {}", event);
            // Create a default account for the new client
            Cuenta cuenta = new Cuenta();
            cuenta.setNumeroCuenta("DEFAULT-" + event.getClienteId());
            cuenta.setTipoCuenta("Ahorro");
            cuenta.setSaldoInicial(0.0);
            cuenta.setEstado(true);
            createCuentaUseCase.execute(cuenta).subscribe();
        };
    }
}