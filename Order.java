package com.example.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Modelo dinâmico de Pedido.
 * Aceita qualquer campo adicional via extraFields (flexibilidade NoSQL).
 */
@Data
public class Order {

    private String id;
    private String status;
    private LocalDateTime createdAt;

    // Campos dinâmicos — o cliente pode enviar qualquer campo no JSON
    private Map<String, Object> extraFields = new HashMap<>();

    public Order() {
        this.id = UUID.randomUUID().toString();
        this.status = "RECEBIDO";
        this.createdAt = LocalDateTime.now();
    }

    @JsonAnySetter
    public void addExtraField(String key, Object value) {
        extraFields.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getExtraFields() {
        return extraFields;
    }
}
