package com.sofka.ms_transactions.domain.service;

import com.sofka.ms_transactions.domain.model.Cuenta;
import com.sofka.ms_transactions.domain.model.Movimientos;
import com.sofka.ms_transactions.domain.repository.MovimientosRepository;
import com.sofka.ms_transactions.event.TransactionCompletedEvent;
import com.sofka.ms_transactions.infrastructure.messaging.EventProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

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
                .flatMap(mov -> {
                    if (mov.getCuentaId() != null) {
                        return cuentaService.findById(mov.getCuentaId())
                                .flatMap(cuenta -> {
                                    double newSaldo = cuenta.getSaldoInicial() + mov.getValor();
                                    if (newSaldo < 0) {
                                        return Mono.error(new RuntimeException("Saldo no disponible"));
                                    }
                                    mov.setSaldo(newSaldo);
                                    cuenta.setSaldoInicial(newSaldo);
                                    return cuentaService.update(cuenta.getCuentaId(), cuenta)
                                            .then(eventProducerService.sendTransactionCompletedEvent(new TransactionCompletedEvent(cuenta.getCuentaId(), mov.getValor(), mov.getTipoMovimiento())))
                                            .then(movimientosRepository.save(mov));
                                });
                    }
                    return movimientosRepository.save(mov);
                });
    }

    public Flux<Movimientos> findByCuentaAndDateRange(Long cuentaId, LocalDate startDate, LocalDate endDate) {
        return movimientosRepository.findByCuentaIdAndFechaBetween(cuentaId, startDate, endDate);
    }
}