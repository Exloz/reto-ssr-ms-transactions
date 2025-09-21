package com.sofka.ms_transactions.domain.repository;

import com.sofka.ms_transactions.domain.model.Movimientos;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface MovimientosRepository {
    Mono<Movimientos> save(Movimientos movimientos);
    Mono<Movimientos> findById(Long id);
    Flux<Movimientos> findByCuentaIdAndFechaBetween(Long cuentaId, LocalDate startDate, LocalDate endDate);
    Flux<Movimientos> findAll();
    Mono<Void> deleteById(Long id);
}