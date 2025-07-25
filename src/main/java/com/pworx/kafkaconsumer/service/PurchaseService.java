package com.pworx.kafkaconsumer.service;

import com.pworx.kafkaconsumer.model.Item;
import com.pworx.kafkaconsumer.model.Purchase;
import com.pworx.kafkaconsumer.repository.PurchaseRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.LongCounter;

import java.util.ArrayList;
import java.util.List;

/**
 * Core domain service that converts incoming purchase messages into production
 * events and persists them.  Encapsulating this logic allows the consumer to
 * remain thin and makes the service reusable by other entry points if needed.
 */
@Service
public class PurchaseService {

    private static final Logger logger = LoggerFactory.getLogger(PurchaseService.class);
    private static final String ASSEMBLY_TOPIC = "assembly-line-topic";
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final EventPublisher eventPublisher;
    private final PurchaseRepository purchaseRepository;
    private final LongCounter purchaseCounter;

    /**
     * Constructs the service with dependencies on the event publisher, MongoDB
     * repository and the metrics {@link Meter}.  Dependencies are injected to
     * keep the service free from initialization logic.
     */
    public PurchaseService(EventPublisher eventPublisher, PurchaseRepository purchaseRepository, Meter meter) {
        this.eventPublisher = eventPublisher;
        this.purchaseRepository = purchaseRepository;
        this.purchaseCounter = meter
                .counterBuilder("purchases_processed_total")
                .setDescription("Total number of purchases processed")
                .build();
    }

    /**
     * Processes a raw purchase message, creating one production event per item
     * and storing the purchase in MongoDB.  Parsing and validation is performed
     * here to keep the consumer layer simple.
     */
    public void process(String message) {
        JsonNode root;
        try {
            root = MAPPER.readTree(message);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid purchase message", e);
        }
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

            try {
                eventPublisher.publish(ASSEMBLY_TOPIC, MAPPER.writeValueAsString(out));
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Failed to serialize production event", e);
            } catch (Exception e) {
                throw new EventPublishException("Failed to publish event", e);
            }

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

        purchaseCounter.add(1);
        logger.info("Emitted {} production events to {}", items.size(), ASSEMBLY_TOPIC);
    }
}
