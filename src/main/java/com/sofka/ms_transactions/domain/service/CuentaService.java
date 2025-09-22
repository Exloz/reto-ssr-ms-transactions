package com.sofka.ms_transactions.domain.service;

import com.sofka.ms_transactions.domain.exception.ResourceNotFoundException;
import com.sofka.ms_transactions.domain.model.Cuenta;
import com.sofka.ms_transactions.domain.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class CuentaService {

    private final CuentaRepository cuentaRepository;

    public Flux<Cuenta> findAll() {
        return cuentaRepository.findAll();
    }

    public Mono<Cuenta> findById(Long cuentaId) {
        return cuentaRepository.findById(cuentaId);
    }

    public Flux<Cuenta> findByClienteId(Long clienteId) {
        return cuentaRepository.findByClienteId(clienteId);
    }

    public Mono<Cuenta> save(Cuenta cuenta) {
        log.info("Saving cuenta: {}", cuenta.getNumeroCuenta());
        if (cuenta.getSaldoActual() == null) {
            cuenta.setSaldoActual(cuenta.getSaldoInicial());
        }
        return cuentaRepository.save(cuenta);
    }

    public Mono<Cuenta> update(Long cuentaId, Cuenta cuenta) {
        return cuentaRepository.findById(cuentaId)
                .flatMap(existing -> {
                    existing.setNumeroCuenta(cuenta.getNumeroCuenta());
                    existing.setTipoCuenta(cuenta.getTipoCuenta());
                    existing.setEstado(cuenta.getEstado());
                    existing.setSaldoInicial(cuenta.getSaldoInicial());
                    if (cuenta.getSaldoActual() != null) {
                        existing.setSaldoActual(cuenta.getSaldoActual());
                    }
                    if (cuenta.getClienteId() != null) {
                        existing.setClienteId(cuenta.getClienteId());
                    }
                    return cuentaRepository.save(existing);
                })
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Cuenta not found")));
    }

    public Mono<Void> delete(Long cuentaId) {
        log.info("Deleting cuenta: {}", cuentaId);
        return cuentaRepository.findById(cuentaId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Cuenta not found")))
                .flatMap(existing -> cuentaRepository.deleteById(cuentaId));
    }
}
