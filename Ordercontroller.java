package com.example.controller;

import com.example.model.Order;
import com.example.producer.OrderProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderProducer orderProducer;

    /**
     * POST /orders
     * Aceita qualquer JSON dinâmico como pedido.
     * Exemplo de body:
     * {
     *   "produto": "Notebook",
     *   "quantidade": 2,
     *   "cliente": "João Silva",
     *   "valorTotal": 4500.00
     * }
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Map<String, Object> body) {
        Order order = new Order();

        // Adiciona todos os campos enviados pelo cliente de forma dinâmica
        body.forEach(order::addExtraField);

        log.info("[ORDER-SERVICE] Novo pedido criado | id={} | campos={}", order.getId(), body.keySet());

        orderProducer.publishOrder(order);

        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/health-check")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of(
                "service", "order-service",
                "status", "UP"
        ));
    }
}
