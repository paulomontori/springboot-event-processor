package com.pworks.kafkaconsumer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.pworks.kafkaconsumer.model.Purchase;

public interface PurchaseRepository extends MongoRepository<Purchase, String> {
}
