package com.example.consumer;

import com.example.model.OrderStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class PrintConsumer {

    // Rastreamento em memória: id -> status atual
    private final Map<String, OrderStatus> statusMap = new ConcurrentHashMap<>();
    private final AtomicInteger totalProcessed = new AtomicInteger(0);

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @KafkaListener(
            topics = "pedidos",
            groupId = "print-group",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consume(
            @Payload Map<String, Object> payload,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset
    ) {
        String orderId = (String) payload.get("id");

        try {
            // Atualiza status: RECEBIDO → EM_PROCESSAMENTO
            statusMap.put(orderId, OrderStatus.EM_PROCESSAMENTO);

            printBanner(payload, partition, offset);

            // Simula processamento
            Thread.sleep(200);

            // Atualiza status: EM_PROCESSAMENTO → IMPRESSO
            statusMap.put(orderId, OrderStatus.IMPRESSO);
            int total = totalProcessed.incrementAndGet();

            log.info("[PRINT-SERVICE] ✅ Pedido impresso com sucesso | id={} | status={} | total-processados={}",
                    orderId, OrderStatus.IMPRESSO, total);

        } catch (InterruptedException e) {
            statusMap.put(orderId, OrderStatus.ERRO);
            Thread.currentThread().interrupt();
            log.error("[PRINT-SERVICE] ❌ Interrompido ao processar pedido | id={}", orderId);
        } catch (Exception e) {
            statusMap.put(orderId, OrderStatus.ERRO);
            log.error("[PRINT-SERVICE] ❌ Erro ao imprimir pedido | id={} | erro={}", orderId, e.getMessage());
        }
    }

    private void printBanner(Map<String, Object> payload, int partition, long offset) {
        String separator = "═".repeat(60);
        System.out.println("\n" + separator);
        System.out.println("  📦  NOVO PEDIDO RECEBIDO  📦");
        System.out.println(separator);
        System.out.printf("  %-20s %s%n", "ID:", payload.get("id"));
        System.out.printf("  %-20s %s%n", "Status:", payload.get("status"));
        System.out.printf("  %-20s %s%n", "Criado em:", payload.get("createdAt"));
        System.out.printf("  %-20s %s%n", "Processado em:", LocalDateTime.now().format(FORMATTER));
        System.out.printf("  %-20s Partition %d | Offset %d%n", "Kafka:", partition, offset);
        System.out.println("  " + "─".repeat(58));
        System.out.println("  DADOS DO PEDIDO:");

        // Imprime campos dinâmicos
        payload.forEach((k, v) -> {
            if (!k.equals("id") && !k.equals("status") && !k.equals("createdAt") && !k.equals("extraFields")) {
                System.out.printf("    %-18s %s%n", k + ":", v);
            }
        });

        // Extra fields se existir
        Object extra = payload.get("extraFields");
        if (extra instanceof Map<?, ?> extraMap && !extraMap.isEmpty()) {
            extraMap.forEach((k, v) -> System.out.printf("    %-18s %s%n", k + ":", v));
        }

        System.out.println(separator + "\n");
    }

    /**
     * Retorna o mapa de status em memória (útil para debug/actuator customizado).
     */
    public Map<String, OrderStatus> getStatusMap() {
        return Map.copyOf(statusMap);
    }

    public int getTotalProcessed() {
        return totalProcessed.get();
    }
}
