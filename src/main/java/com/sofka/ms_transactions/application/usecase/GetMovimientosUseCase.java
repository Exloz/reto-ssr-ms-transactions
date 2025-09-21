package com.sofka.ms_transactions.application.usecase;

import com.sofka.ms_transactions.domain.model.Movimientos;
import com.sofka.ms_transactions.domain.service.MovimientosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetMovimientosUseCase {

    private final MovimientosService movimientosService;

    public Flux<Movimientos> execute(Long cuentaId, LocalDate startDate, LocalDate endDate) {
        log.info("Executing GetMovimientosUseCase for cuentaId: {}", cuentaId);
        return movimientosService.findByCuentaAndDateRange(cuentaId, startDate, endDate);
    }
}