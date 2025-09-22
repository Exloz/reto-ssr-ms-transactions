package com.sofka.ms_transactions.integration;

import com.sofka.ms_transactions.domain.model.Cuenta;
import com.sofka.ms_transactions.domain.model.Movimientos;
import com.sofka.ms_transactions.domain.repository.CuentaRepository;
import com.sofka.ms_transactions.domain.repository.MovimientosRepository;
import com.sofka.ms_transactions.event.TransactionCompletedEvent;
import com.sofka.ms_transactions.infrastructure.messaging.EventProducerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class MovimientosIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private EventProducerService eventProducerService;

    @BeforeEach
    void init() {
        reset(eventProducerService);
        when(eventProducerService.sendTransactionCompletedEvent(any(TransactionCompletedEvent.class))).thenReturn(Mono.empty());
    }

    @Test
    void shouldRegisterMovimientoAndReturnReport() {
        Cuenta cuentaRequest = new Cuenta();
        cuentaRequest.setNumeroCuenta("123456");
        cuentaRequest.setTipoCuenta("Ahorro");
        cuentaRequest.setSaldoInicial(200.0);
        cuentaRequest.setSaldoActual(200.0);
        cuentaRequest.setEstado(true);
        cuentaRequest.setClienteId(42L);

        Cuenta createdCuenta = webTestClient.post()
                .uri("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuentaRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Cuenta.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(createdCuenta);

        Movimientos deposito = new Movimientos();
        deposito.setCuentaId(createdCuenta.getCuentaId());
        deposito.setFecha(LocalDate.now());
        deposito.setTipoMovimiento("Deposito");
        deposito.setValor(150.0);
        deposito.setSaldo(0.0);

        webTestClient.post()
                .uri("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(deposito)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.saldo").isEqualTo(350.0);

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/reportes")
                        .queryParam("clienteId", createdCuenta.getClienteId())
                        .queryParam("startDate", LocalDate.now().minusDays(1))
                        .queryParam("endDate", LocalDate.now().plusDays(1))
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].numeroCuenta").isEqualTo("123456")
                .jsonPath("$[0].movimientos[0].valor").isEqualTo(150.0)
                .jsonPath("$[0].movimientos[0].saldoDisponible").isEqualTo(350.0);

        Movimientos retiro = new Movimientos();
        retiro.setCuentaId(createdCuenta.getCuentaId());
        retiro.setFecha(LocalDate.now());
        retiro.setTipoMovimiento("Retiro");
        retiro.setValor(-600.0);
        retiro.setSaldo(0.0);

        webTestClient.post()
                .uri("/movimientos")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(retiro)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Saldo no disponible");
    }

    @TestConfiguration
    static class RepositoryTestConfiguration {

        @Bean
        @Primary
        CuentaRepository inMemoryCuentaRepository() {
            return new InMemoryCuentaRepository();
        }

        @Bean
        @Primary
        MovimientosRepository inMemoryMovimientosRepository() {
            return new InMemoryMovimientosRepository();
        }
    }

    static class InMemoryCuentaRepository implements CuentaRepository {
        private final Map<Long, Cuenta> data = new ConcurrentHashMap<>();
        private final AtomicLong sequence = new AtomicLong(1);

        @Override
        public Mono<Cuenta> save(Cuenta cuenta) {
            if (cuenta.getCuentaId() == null) {
                cuenta.setCuentaId(sequence.getAndIncrement());
            }
            data.put(cuenta.getCuentaId(), new Cuenta(
                    cuenta.getCuentaId(),
                    cuenta.getNumeroCuenta(),
                    cuenta.getTipoCuenta(),
                    cuenta.getSaldoInicial(),
                    cuenta.getSaldoActual(),
                    cuenta.getEstado(),
                    cuenta.getClienteId()
            ));
            return Mono.just(cuenta);
        }

        @Override
        public Mono<Cuenta> findById(Long id) {
            return Mono.justOrEmpty(data.get(id));
        }

        @Override
        public Mono<Cuenta> findByNumeroCuenta(String numeroCuenta) {
            return Flux.fromIterable(data.values())
                    .filter(c -> numeroCuenta.equals(c.getNumeroCuenta()))
                    .next();
        }

        @Override
        public Flux<Cuenta> findByEstado(Boolean estado) {
            return Flux.fromIterable(data.values())
                    .filter(c -> estado.equals(c.getEstado()));
        }

        @Override
        public Flux<Cuenta> findByClienteId(Long clienteId) {
            return Flux.fromIterable(data.values())
                    .filter(c -> clienteId.equals(c.getClienteId()));
        }

        @Override
        public Flux<Cuenta> findAll() {
            return Flux.fromIterable(new ArrayList<>(data.values()));
        }

        @Override
        public Mono<Void> deleteById(Long id) {
            data.remove(id);
            return Mono.empty();
        }
    }

    static class InMemoryMovimientosRepository implements MovimientosRepository {
        private final Map<Long, Movimientos> data = new ConcurrentHashMap<>();
        private final AtomicLong sequence = new AtomicLong(1);

        @Override
        public Mono<Movimientos> save(Movimientos movimientos) {
            if (movimientos.getMovimientoId() == null) {
                movimientos.setMovimientoId(sequence.getAndIncrement());
            }
            Movimientos copy = new Movimientos(
                    movimientos.getMovimientoId(),
                    movimientos.getFecha(),
                    movimientos.getTipoMovimiento(),
                    movimientos.getValor(),
                    movimientos.getSaldo(),
                    movimientos.getCuentaId()
            );
            data.put(copy.getMovimientoId(), copy);
            return Mono.just(copy);
        }

        @Override
        public Mono<Movimientos> findById(Long id) {
            return Mono.justOrEmpty(data.get(id));
        }

        @Override
        public Flux<Movimientos> findByCuentaIdAndFechaBetween(Long cuentaId, LocalDate startDate, LocalDate endDate) {
            return Flux.fromIterable(data.values())
                    .filter(mov -> cuentaId.equals(mov.getCuentaId())
                            && (mov.getFecha().isEqual(startDate) || mov.getFecha().isAfter(startDate))
                            && (mov.getFecha().isEqual(endDate) || mov.getFecha().isBefore(endDate)));
        }

        @Override
        public Flux<Movimientos> findAll() {
            return Flux.fromIterable(new ArrayList<>(data.values()));
        }

        @Override
        public Mono<Void> deleteById(Long id) {
            data.remove(id);
            return Mono.empty();
        }
    }
}
