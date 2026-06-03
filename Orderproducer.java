package com.example.producer;

import com.example.config.KafkaProducerConfig;
import com.example.model.Order;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publishOrder(Order order) {
        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(KafkaProducerConfig.TOPIC_PEDIDOS, order.getId(), order);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("[PRODUCER] Pedido publicado no Kafka | id={} | offset={}",
                        order.getId(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("[PRODUCER] Falha ao publicar pedido | id={} | erro={}", order.getId(), ex.getMessage());
            }
        });
    }
}
