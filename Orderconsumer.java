package com.example.consumer;

import com.example.model.OrderDocument;
import com.example.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderConsumer {

    private final OrderRepository orderRepository;

    @KafkaListener(
            topics = "pedidos",
            groupId = "processing-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        try {
            log.info("[PROCESSING-SERVICE] Pedido recebido do Kafka | partition={} | offset={} | id={}",
                    partition, offset, payload.get("id"));

            OrderDocument doc = OrderDocument.from(payload);
            doc.setStatus("SALVO");

            OrderDocument saved = orderRepository.save(doc);

            log.info("[PROCESSING-SERVICE] Pedido salvo no MongoDB | id={} | dados={}",
                    saved.getId(), saved.getDados());

        } catch (Exception e) {
            log.error("[PROCESSING-SERVICE] Erro ao processar pedido | erro={}", e.getMessage(), e);
        }
    }
}
