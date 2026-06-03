package com.example.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Documento MongoDB para persistência de pedidos.
 * O campo extraFields permite flexibilidade NoSQL —
 * não é necessário definir schema antes.
 */
@Data
@Document(collection = "pedidos")
public class OrderDocument {

    @Id
    private String id;

    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    // Campos dinâmicos vindos do pedido original
    private Map<String, Object> dados = new HashMap<>();

    public static OrderDocument from(Map<String, Object> payload) {
        OrderDocument doc = new OrderDocument();
        doc.setId((String) payload.get("id"));
        doc.setStatus((String) payload.getOrDefault("status", "PROCESSADO"));
        doc.setProcessedAt(LocalDateTime.now());

        // Tudo que não é campo padrão vai para dados dinâmicos
        payload.forEach((k, v) -> {
            if (!k.equals("id") && !k.equals("status") && !k.equals("createdAt")) {
                doc.getDados().put(k, v);
            }
        });

        // Reconstrói createdAt se presente
        if (payload.get("createdAt") != null) {
            doc.setCreatedAt(LocalDateTime.parse(payload.get("createdAt").toString()));
        }

        return doc;
    }
}
