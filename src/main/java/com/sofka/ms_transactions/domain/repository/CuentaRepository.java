package com.sofka.ms_transactions.domain.repository;

import com.sofka.ms_transactions.domain.model.Cuenta;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CuentaRepository {
    Mono<Cuenta> save(Cuenta cuenta);
    Mono<Cuenta> findById(Long id);
    Mono<Cuenta> findByNumeroCuenta(String numeroCuenta);
    Flux<Cuenta> findByEstado(Boolean estado);
    Flux<Cuenta> findByClienteId(Long clienteId);
    Flux<Cuenta> findAll();
    Mono<Void> deleteById(Long id);
}
