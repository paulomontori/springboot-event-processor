package com.pworx.kafkaconsumer.service;

public interface EventPublisher {
    void publish(String topic, String message) throws Exception;
}
