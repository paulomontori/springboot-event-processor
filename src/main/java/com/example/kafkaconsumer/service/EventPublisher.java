package com.example.kafkaconsumer.service;

public interface EventPublisher {
    void publish(String topic, String message) throws Exception;
}
