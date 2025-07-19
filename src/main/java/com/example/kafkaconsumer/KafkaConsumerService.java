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
import com.example.kafkaconsumer.model.Item;
import com.example.kafkaconsumer.model.Purchase;
import com.example.kafkaconsumer.repository.PurchaseRepository;
import java.util.ArrayList;
import java.util.List;
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
    private final PurchaseRepository purchaseRepository;

    @Autowired
    public KafkaConsumerService(KafkaTemplate<String, String> kafkaTemplate,
                                PurchaseRepository purchaseRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.purchaseRepository = purchaseRepository;
    }

    @KafkaListener(topics = "new_order", groupId = "example-group")
    @Retryable(value = Exception.class, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    public void listen(String message) throws ExecutionException, InterruptedException {
        logger.info("Received message: {}", message);
        JsonNode root = MAPPER.readTree(message);
        String timestamp = root.path("eventTimestamp").asText();
        String purchaseId = root.path("payload").path("purchaseId").asText();
        ArrayNode items = (ArrayNode) root.path("payload").path("items");

        List<Item> itemList = new ArrayList<>();
        for (JsonNode item : items) {
            ObjectNode out = MAPPER.createObjectNode();
            out.put("eventType", "PRODUCTION_ITEM");
            out.put("eventTimestamp", timestamp);
            ObjectNode payload = out.putObject("payload");
            payload.put("purchaseId", purchaseId);
            payload.put("productId", item.path("productId").asText());
            payload.put("quantity", item.path("quantity").asInt());
            kafkaTemplate.send(ASSEMBLY_TOPIC, MAPPER.writeValueAsString(out)).get();

            Item orderItem = new Item();
            orderItem.setProductId(item.path("productId").asText());
            orderItem.setQuantity(item.path("quantity").asInt());
            itemList.add(orderItem);
        }
        Purchase purchase = new Purchase();
        purchase.setPurchaseId(purchaseId);
        purchase.setEventTimestamp(timestamp);
        purchase.setItems(itemList);
        purchaseRepository.save(purchase);
        logger.info("Emitted {} production events to {}", items.size(), ASSEMBLY_TOPIC);
    }

    @Recover
    public void recover(Exception e, String message) {
        logger.error("Failed to process message after retries: {}", message, e);
    }
}
