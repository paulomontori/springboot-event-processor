package com.pworx.kafkaconsumer.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PurchaseTest {

    @Test
    void gettersAndSetters() {
        Purchase purchase = new Purchase();
        purchase.setPurchaseId("id");
        purchase.setEventTimestamp("ts");
        Item item = new Item();
        item.setProductId("p");
        item.setQuantity(1);
        purchase.setItems(List.of(item));

        assertEquals("id", purchase.getPurchaseId());
        assertEquals("ts", purchase.getEventTimestamp());
        assertEquals(1, purchase.getItems().size());
        assertEquals("p", purchase.getItems().get(0).getProductId());
    }
}
