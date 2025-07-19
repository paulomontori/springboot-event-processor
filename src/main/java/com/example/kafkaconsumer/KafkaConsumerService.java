package com.example.kafkaconsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

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
    public void listen(String message) {
        logger.info("Received message: {}", message);
        kafkaTemplate.send(ASSEMBLY_TOPIC, message);
        kafkaTemplate.send(INVENTORY_TOPIC, message);
        logger.info("Emitted events to {} and {}", ASSEMBLY_TOPIC, INVENTORY_TOPIC);
    }
}
