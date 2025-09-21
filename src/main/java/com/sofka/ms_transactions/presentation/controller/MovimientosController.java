package com.sofka.ms_transactions.presentation.controller;

import com.sofka.ms_transactions.domain.model.Movimientos;
import com.sofka.ms_transactions.application.usecase.GetMovimientosUseCase;
import com.sofka.ms_transactions.domain.service.MovimientosService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;
import java.time.LocalDate;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
@Slf4j
public class MovimientosController {

    private final MovimientosService movimientosService;
    private final GetMovimientosUseCase getMovimientosUseCase;

    @GetMapping
    public Flux<Movimientos> getAllMovimientos() {
        return movimientosService.findAll();
    }

    @PostMapping
    public Mono<ResponseEntity<Movimientos>> createMovimiento(@Valid @RequestBody Movimientos movimientos) {
        return movimientosService.save(movimientos)
                .map(m -> ResponseEntity.status(HttpStatus.CREATED).body(m))
                .onErrorResume(e -> {
                    log.error("Error creating movimiento: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    @GetMapping("/reportes")
    public Flux<Movimientos> getReporte(
            @RequestParam Long cuentaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return getMovimientosUseCase.execute(cuentaId, startDate, endDate);
    }
}