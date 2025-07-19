package com.pworks.kafkaconsumer.service;

public interface EventPublisher {
    void publish(String topic, String message) throws Exception;
}
