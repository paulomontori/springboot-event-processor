package com.pworx.kafkaconsumer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * MongoDB document representing a purchase event.  Storing the event allows the
 * system to maintain a history of all received orders for auditing and
 * traceability.  The entity is kept simple so repositories can persist it
 * without additional mapping code.
 */
@Document(collection = "purchases")
public class Purchase {
    @Id
    private String purchaseId;
    private String eventTimestamp;
    private List<Item> items;

    /** Identifier provided by the upstream system. */
    public String getPurchaseId() {
        return purchaseId;
    }

    /** Sets the unique identifier for this purchase. */
    public void setPurchaseId(String purchaseId) {
        this.purchaseId = purchaseId;
    }

    /** Timestamp for when the event was produced. */
    public String getEventTimestamp() {
        return eventTimestamp;
    }

    /** Sets the timestamp for the event. */
    public void setEventTimestamp(String eventTimestamp) {
        this.eventTimestamp = eventTimestamp;
    }

    /** List of items included in the purchase. */
    public List<Item> getItems() {
        return items;
    }

    /** Sets the items included in the purchase. */
    public void setItems(List<Item> items) {
        this.items = items;
    }
}
