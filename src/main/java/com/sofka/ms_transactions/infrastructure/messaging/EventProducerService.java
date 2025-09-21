package com.sofka.ms_transactions.infrastructure.messaging;

import com.sofka.ms_transactions.event.TransactionCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventProducerService {

    private final StreamBridge streamBridge;

    public Mono<Void> sendTransactionCompletedEvent(TransactionCompletedEvent event) {
        log.info("Sending TransactionCompletedEvent: {}", event);
        return Mono.fromRunnable(() -> streamBridge.send("transactionCompleted-out-0", event));
    }
}