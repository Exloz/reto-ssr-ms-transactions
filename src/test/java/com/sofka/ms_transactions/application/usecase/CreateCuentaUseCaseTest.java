package com.sofka.ms_transactions.application.usecase;

import com.sofka.ms_transactions.domain.model.Cuenta;
import com.sofka.ms_transactions.domain.service.CuentaService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateCuentaUseCaseTest {

    @Mock
    private CuentaService cuentaService;

    @InjectMocks
    private CreateCuentaUseCase createCuentaUseCase;

    @Test
    public void shouldCreateCuenta() {
        Cuenta cuenta = new Cuenta();
        cuenta.setNumeroCuenta("123456");
        cuenta.setTipoCuenta("Ahorro");
        cuenta.setSaldoInicial(1000.0);
        cuenta.setSaldoActual(1000.0);
        cuenta.setEstado(true);
        cuenta.setClienteId(1L);

        when(cuentaService.save(any(Cuenta.class))).thenReturn(Mono.just(cuenta));

        Mono<Cuenta> result = createCuentaUseCase.execute(cuenta);

        StepVerifier.create(result)
                .expectNextMatches(c -> "123456".equals(c.getNumeroCuenta()) && c.getSaldoActual().equals(1000.0))
                .verifyComplete();

        verify(cuentaService, times(1)).save(cuenta);
    }
}
