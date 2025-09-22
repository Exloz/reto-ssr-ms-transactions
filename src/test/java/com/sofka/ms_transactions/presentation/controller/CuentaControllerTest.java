package com.sofka.ms_transactions.presentation.controller;

import com.sofka.ms_transactions.domain.model.Cuenta;
import com.sofka.ms_transactions.application.usecase.CreateCuentaUseCase;
import com.sofka.ms_transactions.domain.service.CuentaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(CuentaController.class)
public class CuentaControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private CreateCuentaUseCase createCuentaUseCase;

    @MockBean
    private CuentaService cuentaService;

    @Test
    public void shouldCreateCuenta() throws Exception {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta("123456");
        cuenta.setTipoCuenta("Ahorro");
        cuenta.setSaldoInicial(1000.0);
        cuenta.setSaldoActual(1000.0);
        cuenta.setEstado(true);
        cuenta.setClienteId(1L);

        when(createCuentaUseCase.execute(any(Cuenta.class))).thenReturn(Mono.just(cuenta));

        webTestClient.post()
                .uri("/cuentas")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(cuenta)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.numeroCuenta").isEqualTo("123456");
    }
}
