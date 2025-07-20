package com.pworx.kafkaconsumer.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void gettersAndSetters() {
        Item item = new Item();
        item.setProductId("P1");
        item.setQuantity(3);

        assertEquals("P1", item.getProductId());
        assertEquals(3, item.getQuantity());
    }
}
