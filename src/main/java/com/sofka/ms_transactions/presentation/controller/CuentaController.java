package com.sofka.ms_transactions.presentation.controller;

import com.sofka.ms_transactions.domain.model.Cuenta;
import com.sofka.ms_transactions.application.usecase.CreateCuentaUseCase;
import com.sofka.ms_transactions.domain.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
@Slf4j
public class CuentaController {

    private final CuentaService cuentaService;
    private final CreateCuentaUseCase createCuentaUseCase;
    private static final ResponseEntity<Void> NO_CONTENT_RESPONSE = new ResponseEntity<>(HttpStatus.NO_CONTENT);
    private static final ResponseEntity<Void> NOT_FOUND_RESPONSE = new ResponseEntity<>(HttpStatus.NOT_FOUND);

    @GetMapping
    @Operation(summary = "Get all cuentas")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation")
    })
    public Flux<Cuenta> getAllCuentas() {
        return cuentaService.findAll();
    }

    @GetMapping("/{cuentaId}")
    @Operation(summary = "Get cuenta by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Cuenta not found")
    })
    public Mono<ResponseEntity<Cuenta>> getCuentaById(@PathVariable Long cuentaId) {
        return cuentaService.findById(cuentaId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Cuenta created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public Mono<ResponseEntity<Cuenta>> createCuenta(@Valid @RequestBody Cuenta cuenta) {
        return createCuentaUseCase.execute(cuenta)
                .map(c -> ResponseEntity.status(HttpStatus.CREATED).body(c))
                 .onErrorResume(e -> Mono.fromCallable(() -> ResponseEntity.<Cuenta>badRequest().build()));
    }

    @PutMapping("/{cuentaId}")
    @Operation(summary = "Update cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Cuenta not found")
    })
    public Mono<ResponseEntity<Cuenta>> updateCuenta(@PathVariable Long cuentaId, @Valid @RequestBody Cuenta cuenta) {
        return cuentaService.update(cuentaId, cuenta)
                .map(ResponseEntity::ok)
                 .onErrorResume(e -> Mono.fromCallable(() -> ResponseEntity.<Cuenta>notFound().build()));
    }

    @DeleteMapping("/{cuentaId}")
    @Operation(summary = "Delete cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Cuenta deleted"),
            @ApiResponse(responseCode = "404", description = "Cuenta not found")
    })
    public Mono<ResponseEntity<Void>> deleteCuenta(@PathVariable Long cuentaId) {
        return cuentaService.findById(cuentaId)
                .flatMap(existing -> cuentaService.delete(cuentaId)
                        .thenReturn(NO_CONTENT_RESPONSE))
                .switchIfEmpty(Mono.just(NOT_FOUND_RESPONSE));
    }
}
