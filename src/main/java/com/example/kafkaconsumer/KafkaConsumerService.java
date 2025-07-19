package com.example.kafkaconsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Recover;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private static final String ASSEMBLY_TOPIC = "assembly-line-topic";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    public KafkaConsumerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = "new_order", groupId = "example-group")
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void listen(String message) throws ExecutionException, InterruptedException {
        logger.info("Received message: {}", message);
        JsonNode root = MAPPER.readTree(message);
        String timestamp = root.path("eventTimestamp").asText();
        String purchaseId = root.path("payload").path("purchaseId").asText();
        ArrayNode items = (ArrayNode) root.path("payload").path("items");

        for (JsonNode item : items) {
            ObjectNode out = MAPPER.createObjectNode();
            out.put("eventType", "PRODUCTION_ITEM");
            out.put("eventTimestamp", timestamp);
            ObjectNode payload = out.putObject("payload");
            payload.put("purchaseId", purchaseId);
            payload.put("productId", item.path("productId").asText());
            payload.put("quantity", item.path("quantity").asInt());
            kafkaTemplate.send(ASSEMBLY_TOPIC, MAPPER.writeValueAsString(out)).get();
        }
        logger.info("Emitted {} production events to {}", items.size(), ASSEMBLY_TOPIC);
    }

    @Recover
    public void recover(Exception e, String message) {
        logger.error("Failed to process message after retries: {}", message, e);
    }
}
