package com.sofka.ms_transactions.presentation.controller;

import com.sofka.ms_transactions.domain.exception.ResourceNotFoundException;
import com.sofka.ms_transactions.domain.exception.SaldoNoDisponibleException;
import com.sofka.ms_transactions.domain.model.Movimientos;
import com.sofka.ms_transactions.domain.service.MovimientosService;
import com.sofka.ms_transactions.presentation.dto.CreateMovimientoRequest;
import com.sofka.ms_transactions.presentation.dto.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/movimientos")
@RequiredArgsConstructor
@Slf4j
public class MovimientosController {

    private final MovimientosService movimientosService;
    private static final ResponseEntity<Void> NO_CONTENT_RESPONSE = ResponseEntity.noContent().build();
    private static final ResponseEntity<Void> NOT_FOUND_RESPONSE = ResponseEntity.notFound().build();

    @GetMapping
    public Flux<Movimientos> getAllMovimientos() {
        return movimientosService.findAll();
    }

    @PostMapping
    public Mono<ResponseEntity<Object>> createMovimiento(@Valid @RequestBody CreateMovimientoRequest request) {
        Movimientos movimientos = new Movimientos();
        movimientos.setFecha(request.getFecha());
        movimientos.setTipoMovimiento(request.getTipoMovimiento());
        movimientos.setValor(request.getValor());
        movimientos.setCuentaId(request.getCuentaId());

        return movimientosService.save(movimientos)
                .map(m -> ResponseEntity.status(HttpStatus.CREATED).body((Object) m))
                .onErrorResume(SaldoNoDisponibleException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()))))
                .onErrorResume(ResourceNotFoundException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()))));
    }

    @GetMapping("/{movimientoId}")
    public Mono<ResponseEntity<Movimientos>> getMovimientoById(@PathVariable Long movimientoId) {
        return movimientosService.findById(movimientoId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PutMapping("/{movimientoId}")
    public Mono<ResponseEntity<Object>> updateMovimiento(@PathVariable Long movimientoId, @Valid @RequestBody Movimientos movimientos) {
        return movimientosService.update(movimientoId, movimientos)
                .map(updated -> ResponseEntity.ok().body((Object) updated))
                .onErrorResume(SaldoNoDisponibleException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(e.getMessage()))))
                .onErrorResume(ResourceNotFoundException.class, e -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()))));
    }

    @DeleteMapping("/{movimientoId}")
    public Mono<ResponseEntity<Void>> deleteMovimiento(@PathVariable Long movimientoId) {
        return movimientosService.delete(movimientoId)
                .thenReturn(NO_CONTENT_RESPONSE)
                .onErrorResume(ResourceNotFoundException.class, e -> Mono.just(NOT_FOUND_RESPONSE));
    }
}
