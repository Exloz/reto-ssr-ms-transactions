package com.sofka.ms_transactions.domain.service;

import com.sofka.ms_transactions.domain.exception.ResourceNotFoundException;
import com.sofka.ms_transactions.domain.exception.SaldoNoDisponibleException;
import com.sofka.ms_transactions.domain.model.Cuenta;
import com.sofka.ms_transactions.domain.model.Movimientos;
import com.sofka.ms_transactions.domain.repository.MovimientosRepository;
import com.sofka.ms_transactions.event.TransactionCompletedEvent;
import com.sofka.ms_transactions.infrastructure.messaging.EventProducerService;
import com.sofka.ms_transactions.presentation.dto.AccountStatementResponse;
import com.sofka.ms_transactions.presentation.dto.MovementDetailResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovimientosService {

    private final MovimientosRepository movimientosRepository;
    private final CuentaService cuentaService;
    private final EventProducerService eventProducerService;

    public Flux<Movimientos> findAll() {
        return movimientosRepository.findAll();
    }

    public Mono<Movimientos> save(Movimientos movimientos) {
        return Mono.just(movimientos)
                .flatMap(mov -> cuentaService.findById(mov.getCuentaId())
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Cuenta not found")))
                        .flatMap(cuenta -> {
                            double saldoBase = cuenta.getSaldoActual() != null ? cuenta.getSaldoActual() : cuenta.getSaldoInicial();
                            return aplicarMovimiento(cuenta, mov, saldoBase);
                        })
                );
    }

    public Flux<Movimientos> findByCuentaAndDateRange(Long cuentaId, LocalDate startDate, LocalDate endDate) {
        return movimientosRepository.findByCuentaIdAndFechaBetween(cuentaId, startDate, endDate);
    }

    public Mono<Movimientos> findById(Long movimientoId) {
        return movimientosRepository.findById(movimientoId);
    }

    public Mono<Movimientos> update(Long movimientoId, Movimientos movimientos) {
        return movimientosRepository.findById(movimientoId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Movimiento not found")))
                .flatMap(existing -> cuentaService.findById(existing.getCuentaId())
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Cuenta not found")))
                        .flatMap(cuenta -> {
                            double saldoActual = cuenta.getSaldoActual() != null ? cuenta.getSaldoActual() : cuenta.getSaldoInicial();
                            double saldoBase = saldoActual - existing.getValor();
                            movimientos.setMovimientoId(movimientoId);
                            movimientos.setCuentaId(existing.getCuentaId());
                            return aplicarMovimiento(cuenta, movimientos, saldoBase);
                        }));
    }

    public Mono<Void> delete(Long movimientoId) {
        return movimientosRepository.findById(movimientoId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Movimiento not found")))
                .flatMap(existing -> cuentaService.findById(existing.getCuentaId())
                        .switchIfEmpty(Mono.error(new ResourceNotFoundException("Cuenta not found")))
                        .flatMap(cuenta -> {
                            double saldoActual = cuenta.getSaldoActual() != null ? cuenta.getSaldoActual() : cuenta.getSaldoInicial();
                            double newSaldo = saldoActual - existing.getValor();
                            cuenta.setSaldoActual(newSaldo);
                            return cuentaService.update(cuenta.getCuentaId(), cuenta)
                                    .then(movimientosRepository.deleteById(movimientoId));
                        }));
    }

    public Flux<AccountStatementResponse> generateAccountStatement(Long clienteId, LocalDate startDate, LocalDate endDate) {
        return cuentaService.findByClienteId(clienteId)
                .flatMap(cuenta -> movimientosRepository.findByCuentaIdAndFechaBetween(cuenta.getCuentaId(), startDate, endDate)
                        .map(mov -> new MovementDetailResponse(mov.getFecha(), mov.getTipoMovimiento(), mov.getValor(), mov.getSaldo()))
                        .collectList()
                        .map(movimientos -> buildAccountStatement(clienteId, cuenta, movimientos)));
    }

    @Transactional
    private Mono<Movimientos> aplicarMovimiento(Cuenta cuenta, Movimientos movimientos, double saldoBase) {
        double nuevoSaldo = saldoBase + movimientos.getValor();
        if (nuevoSaldo < 0) {
            return Mono.error(new SaldoNoDisponibleException("Saldo no disponible"));
        }
        movimientos.setCuentaId(cuenta.getCuentaId());
        movimientos.setSaldo(nuevoSaldo);
        cuenta.setSaldoActual(nuevoSaldo);

        return cuentaService.update(cuenta.getCuentaId(), cuenta)
                .then(movimientosRepository.save(movimientos))
                .flatMap(saved -> eventProducerService.sendTransactionCompletedEvent(new TransactionCompletedEvent(cuenta.getCuentaId(), movimientos.getValor(), movimientos.getTipoMovimiento()))
                        .thenReturn(saved));
    }

    private AccountStatementResponse buildAccountStatement(Long clienteId, Cuenta cuenta, List<MovementDetailResponse> movimientos) {
        return new AccountStatementResponse(
                clienteId,
                cuenta.getCuentaId(),
                cuenta.getNumeroCuenta(),
                cuenta.getTipoCuenta(),
                cuenta.getSaldoInicial(),
                cuenta.getSaldoActual(),
                movimientos
        );
    }
}
