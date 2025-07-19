package com.example.kafkaconsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Recover;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private static final String ASSEMBLY_TOPIC = "assembly-line-topic";
    private static final String INVENTORY_TOPIC = "inventory-topic";

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaConsumerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "example-topic", groupId = "example-group")
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void listen(String message) throws ExecutionException, InterruptedException {
        logger.info("Received message: {}", message);
        kafkaTemplate.send(ASSEMBLY_TOPIC, message).get();
        kafkaTemplate.send(INVENTORY_TOPIC, message).get();
        logger.info("Emitted events to {} and {}", ASSEMBLY_TOPIC, INVENTORY_TOPIC);
    }

    @Recover
    public void recover(Exception e, String message) {
        logger.error("Failed to process message after retries: {}", message, e);
    }
}
