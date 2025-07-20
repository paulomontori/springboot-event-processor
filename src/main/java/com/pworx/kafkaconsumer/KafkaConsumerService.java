package com.pworx.kafkaconsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import com.pworx.kafkaconsumer.service.PurchaseService;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Recover;

@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final PurchaseService purchaseService;
    public KafkaConsumerService(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @KafkaListener(topics = "new_order", groupId = "example-group")
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void listen(String message) {
        logger.info("Received message: {}", message);
        purchaseService.process(message);
    }

    @Recover
    public void recover(Exception e, String message) {
        logger.error("Failed to process message after retries: {}", message, e);
    }
}
