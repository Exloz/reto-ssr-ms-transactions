package com.sofka.ms_transactions.presentation.controller;

import com.sofka.ms_transactions.application.usecase.GetAccountStatementUseCase;
import com.sofka.ms_transactions.presentation.dto.AccountStatementResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReportesController {

    private final GetAccountStatementUseCase getAccountStatementUseCase;

    @GetMapping
    public Flux<AccountStatementResponse> getReporte(
            @RequestParam Long clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return getAccountStatementUseCase.execute(clienteId, startDate, endDate);
    }
}
