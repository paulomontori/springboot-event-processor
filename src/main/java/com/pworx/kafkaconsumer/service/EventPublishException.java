package com.pworx.kafkaconsumer.service;

/**
 * Exception thrown when an event cannot be published.  Using a custom runtime
 * exception allows the caller to decide how to react to publishing failures
 * while keeping checked exceptions out of the service layer API.
 */
public class EventPublishException extends RuntimeException {

    /**
     * Creates a new instance with a message and the underlying cause.
     */
    public EventPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
