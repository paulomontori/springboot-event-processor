package com.pworx.kafkaconsumer.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventPublishExceptionTest {

    @Test
    void storesMessageAndCause() {
        Exception cause = new Exception("cause");
        EventPublishException ex = new EventPublishException("msg", cause);
        assertEquals("msg", ex.getMessage());
        assertEquals(cause, ex.getCause());
    }
}
