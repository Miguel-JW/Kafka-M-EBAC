package com.example.controller;

import com.example.consumer.PrintConsumer;
import com.example.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/status")
@RequiredArgsConstructor
public class StatusController {

    private final PrintConsumer printConsumer;

    /**
     * GET /status
     * Retorna o status atual de todos os pedidos processados em memória.
     */
    @GetMapping
    public ResponseEntity<Map<String, OrderStatus>> getAllStatuses() {
        return ResponseEntity.ok(printConsumer.getStatusMap());
    }

    /**
     * GET /status/{orderId}
     * Retorna o status de um pedido específico.
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getStatus(@PathVariable String orderId) {
        Map<String, OrderStatus> statusMap = printConsumer.getStatusMap();

        if (!statusMap.containsKey(orderId)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(Map.of(
                "orderId", orderId,
                "status", statusMap.get(orderId),
                "totalProcessados", printConsumer.getTotalProcessed()
        ));
    }

    /**
     * GET /status/summary
     * Retorna um resumo com total de pedidos por status.
     */
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary() {
        Map<String, OrderStatus> statusMap = printConsumer.getStatusMap();

        long impressos = statusMap.values().stream().filter(s -> s == OrderStatus.IMPRESSO).count();
        long erros = statusMap.values().stream().filter(s -> s == OrderStatus.ERRO).count();
        long emProcessamento = statusMap.values().stream().filter(s -> s == OrderStatus.EM_PROCESSAMENTO).count();

        return ResponseEntity.ok(Map.of(
                "total", printConsumer.getTotalProcessed(),
                "impressos", impressos,
                "emProcessamento", emProcessamento,
                "erros", erros
        ));
    }
}
