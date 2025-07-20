package com.pworx.kafkaconsumer.service;

public class EventPublishException extends RuntimeException {
    public EventPublishException(String message, Throwable cause) {
        super(message, cause);
    }
}
