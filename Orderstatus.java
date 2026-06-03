package com.example.model;

/**
 * Estados possíveis de um pedido no print-service.
 */
public enum OrderStatus {
    RECEBIDO,
    EM_PROCESSAMENTO,
    IMPRESSO,
    CONCLUIDO,
    ERRO
}
