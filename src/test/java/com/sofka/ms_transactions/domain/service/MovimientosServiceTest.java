package com.sofka.ms_transactions.domain.service;

import com.sofka.ms_transactions.domain.exception.SaldoNoDisponibleException;
import com.sofka.ms_transactions.domain.model.Cuenta;
import com.sofka.ms_transactions.domain.model.Movimientos;
import com.sofka.ms_transactions.domain.repository.MovimientosRepository;
import com.sofka.ms_transactions.infrastructure.messaging.EventProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MovimientosServiceTest {

    @Mock
    private MovimientosRepository movimientosRepository;

    @Mock
    private CuentaService cuentaService;

    @Mock
    private EventProducerService eventProducerService;

    @InjectMocks
    private MovimientosService movimientosService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldFailWhenSaldoNotAvailable() {
        Cuenta cuenta = new Cuenta();
        cuenta.setCuentaId(1L);
        cuenta.setNumeroCuenta("123");
        cuenta.setTipoCuenta("Ahorro");
        cuenta.setSaldoInicial(50.0);
        cuenta.setSaldoActual(50.0);
        cuenta.setEstado(true);
        cuenta.setClienteId(99L);

        Movimientos retiro = new Movimientos();
        retiro.setMovimientoId(null);
        retiro.setCuentaId(1L);
        retiro.setFecha(LocalDate.now());
        retiro.setTipoMovimiento("Retiro");
        retiro.setValor(-100.0);
        retiro.setSaldo(0.0);

        when(cuentaService.findById(1L)).thenReturn(Mono.just(cuenta));

        StepVerifier.create(movimientosService.save(retiro))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof SaldoNoDisponibleException);
                    assertEquals("Saldo no disponible", error.getMessage());
                })
                .verify();

        verify(movimientosRepository, never()).save(any(Movimientos.class));
        verify(eventProducerService, never()).sendTransactionCompletedEvent(any());
    }
}
