package com.pworx.kafkaconsumer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.pworx.kafkaconsumer.model.Purchase;

/**
 * Spring Data repository for persisting {@link Purchase} documents.  Using the
 * {@link MongoRepository} interface provides CRUD operations without requiring
 * boilerplate data access code.
 */
public interface PurchaseRepository extends MongoRepository<Purchase, String> {
}
