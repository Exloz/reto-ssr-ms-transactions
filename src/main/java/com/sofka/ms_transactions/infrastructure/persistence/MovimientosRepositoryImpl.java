package com.sofka.ms_transactions.infrastructure.persistence;

import com.sofka.ms_transactions.domain.model.Movimientos;
import com.sofka.ms_transactions.domain.repository.MovimientosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
@Slf4j
public class MovimientosRepositoryImpl implements MovimientosRepository {

    private final R2dbcEntityTemplate template;

    @Override
    public Mono<Movimientos> save(Movimientos movimientos) {
        return template.insert(movimientos);
    }

    @Override
    public Mono<Movimientos> findById(Long id) {
        return template.select(Movimientos.class)
                .from("movimientos")
                .matching(Query.query(Criteria.where("movimiento_id").is(id)))
                .one();
    }

    @Override
    public Flux<Movimientos> findByCuentaIdAndFechaBetween(Long cuentaId, LocalDate startDate, LocalDate endDate) {
        return template.select(Movimientos.class)
                .from("movimientos")
                .matching(Query.query(Criteria.where("cuenta_id").is(cuentaId)
                        .and("fecha").between(startDate, endDate)))
                .all();
    }

    @Override
    public Flux<Movimientos> findAll() {
        return template.select(Movimientos.class)
                .from("movimientos")
                .all();
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return template.delete(Movimientos.class)
                .from("movimientos")
                .matching(Query.query(Criteria.where("movimiento_id").is(id)))
                .all()
                .then();
    }
}