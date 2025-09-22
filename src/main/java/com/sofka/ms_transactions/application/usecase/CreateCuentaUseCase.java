package com.sofka.ms_transactions.application.usecase;

import com.sofka.ms_transactions.domain.model.Cuenta;
import com.sofka.ms_transactions.domain.service.CuentaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateCuentaUseCase {

    private final CuentaService cuentaService;

    public Mono<Cuenta> execute(Cuenta cuenta) {
        log.info("Executing CreateCuentaUseCase for numeroCuenta: {}", cuenta.getNumeroCuenta());
        return cuentaService.save(cuenta);
    }
}