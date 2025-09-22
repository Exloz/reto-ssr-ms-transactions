package com.sofka.ms_transactions.application.usecase;

import com.sofka.ms_transactions.domain.service.MovimientosService;
import com.sofka.ms_transactions.presentation.dto.AccountStatementResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetAccountStatementUseCase {

    private final MovimientosService movimientosService;

    public Flux<AccountStatementResponse> execute(Long clienteId, LocalDate startDate, LocalDate endDate) {
        log.info("Executing GetAccountStatementUseCase for clienteId: {}", clienteId);
        return movimientosService.generateAccountStatement(clienteId, startDate, endDate);
    }
}
