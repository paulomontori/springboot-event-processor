package com.example.kafkaconsumer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.kafkaconsumer.model.Purchase;

public interface PurchaseRepository extends MongoRepository<Purchase, String> {
}
