package com.pworx.kafkaconsumer.model;

/**
 * Simple value object representing a single line item of a purchase.  It is
 * kept free of persistence or business logic so it can be reused by different
 * storage technologies or services.
 */
public class Item {
    private String productId;
    private int quantity;

    /** Returns the product identifier. */
    public String getProductId() {
        return productId;
    }

    /** Sets the product identifier. */
    public void setProductId(String productId) {
        this.productId = productId;
    }

    /** Returns the quantity purchased. */
    public int getQuantity() {
        return quantity;
    }

    /** Sets the quantity purchased. */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
