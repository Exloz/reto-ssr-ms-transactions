package com.sofka.ms_transactions.infrastructure.messaging;

import com.sofka.ms_transactions.domain.model.Cuenta;
import com.sofka.ms_transactions.event.ClienteCreatedEvent;
import com.sofka.ms_transactions.application.usecase.CreateCuentaUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(value = "app.kafka.cliente-created.enabled", havingValue = "true", matchIfMissing = true)
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
            cuenta.setSaldoActual(0.0);
            cuenta.setEstado(true);
            cuenta.setClienteId(event.getClienteId());
            createCuentaUseCase.execute(cuenta)
                    .doOnSuccess(savedCuenta -> log.info("Default cuenta {} created for clienteId={}", savedCuenta.getCuentaId(), event.getClienteId()))
                    .doOnError(error -> log.error("Failed to create default cuenta for clienteId={}", event.getClienteId(), error))
                    .subscribe();
        };
    }
}
