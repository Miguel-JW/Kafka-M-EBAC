package com.example.repository;

import com.example.model.OrderDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends MongoRepository<OrderDocument, String> {

    List<OrderDocument> findByStatus(String status);
}
