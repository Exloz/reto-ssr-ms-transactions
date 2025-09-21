package com.sofka.ms_transactions.infrastructure.persistence;

import com.sofka.ms_transactions.domain.model.Cuenta;
import com.sofka.ms_transactions.domain.repository.CuentaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CuentaRepositoryImpl implements CuentaRepository {

    private final R2dbcEntityTemplate template;

    @Override
    public Mono<Cuenta> save(Cuenta cuenta) {
        return template.insert(cuenta);
    }

    @Override
    public Mono<Cuenta> findById(Long id) {
        return template.select(Cuenta.class)
                .from("cuentas")
                .matching(Query.query(Criteria.where("cuenta_id").is(id)))
                .one();
    }

    @Override
    public Mono<Cuenta> findByNumeroCuenta(String numeroCuenta) {
        return template.select(Cuenta.class)
                .from("cuentas")
                .matching(Query.query(Criteria.where("numero_cuenta").is(numeroCuenta)))
                .one();
    }

    @Override
    public Flux<Cuenta> findByEstado(Boolean estado) {
        return template.select(Cuenta.class)
                .from("cuentas")
                .matching(Query.query(Criteria.where("estado").is(estado)))
                .all();
    }

    @Override
    public Flux<Cuenta> findAll() {
        return template.select(Cuenta.class)
                .from("cuentas")
                .all();
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return template.delete(Cuenta.class)
                .from("cuentas")
                .matching(Query.query(Criteria.where("cuenta_id").is(id)))
                .all()
                .then();
    }
}