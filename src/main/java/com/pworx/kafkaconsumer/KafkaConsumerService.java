package com.pworx.kafkaconsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import com.pworx.kafkaconsumer.service.PurchaseService;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Recover;

/**
 * Consumes raw purchase messages from Kafka and delegates the business logic to
 * {@link PurchaseService}.  The class isolates interaction with the messaging
 * layer so that the rest of the application remains unaware of Kafka specifics.
 */
@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final PurchaseService purchaseService;
    /**
     * Constructs a new consumer with the {@link PurchaseService} dependency.
     * Wiring through the constructor keeps the class stateless and easily
     * testable.
     */
    public KafkaConsumerService(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    /**
     * Handles incoming messages from the {@code new_order} topic.  The method is
     * retried if any exception is thrown so transient failures do not drop
     * events.
     */
    @KafkaListener(topics = "new_order", groupId = "example-group")
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void listen(String message) {
        logger.info("Received message: {}", message);
        purchaseService.process(message);
    }

    /**
     * Logs messages that could not be processed even after retrying.  Recovery
     * logic is intentionally simple here but could be extended to route the
     * event to a dead letter topic.
     */
    @Recover
    public void recover(Exception e, String message) {
        logger.error("Failed to process message after retries: {}", message, e);
    }
}
