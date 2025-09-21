package com.sofka.ms_transactions.domain.service;

import com.sofka.ms_transactions.domain.model.Cuenta;
import com.sofka.ms_transactions.domain.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CuentaService {

    private final CuentaRepository cuentaRepository;

    public Flux<Cuenta> findAll() {
        return cuentaRepository.findAll();
    }

    public Mono<Cuenta> findById(Long cuentaId) {
        return cuentaRepository.findById(cuentaId);
    }

    public Mono<Cuenta> save(Cuenta cuenta) {
        log.info("Saving cuenta: {}", cuenta.getNumeroCuenta());
        return cuentaRepository.save(cuenta);
    }

    public Mono<Cuenta> update(Long cuentaId, Cuenta cuenta) {
        return cuentaRepository.findById(cuentaId)
                .flatMap(existing -> {
                    cuenta.setCuentaId(cuentaId);
                    return cuentaRepository.save(cuenta);
                })
                .switchIfEmpty(Mono.error(new RuntimeException("Cuenta not found")));
    }

    public Mono<Void> delete(Long cuentaId) {
        log.info("Deleting cuenta: {}", cuentaId);
        return cuentaRepository.deleteById(cuentaId);
    }
}