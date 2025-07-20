package com.pworx.kafkaconsumer.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.concurrent.ExecutionException;

/**
 * Kafka implementation of {@link EventPublisher}.  Wrapping the
 * {@link KafkaTemplate} behind this class ensures that publishing can be
 * mocked in tests and swapped for another transport if requirements change.
 */
@Component
public class KafkaEventPublisher implements EventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Creates a new publisher using the provided {@link KafkaTemplate} which is
     * managed by Spring Boot.  Constructor injection keeps the dependency
     * explicit.
     */
    public KafkaEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Sends a message synchronously to ensure ordering and error propagation.
     */
    @Override
    public void publish(String topic, String message) throws ExecutionException, InterruptedException {
        kafkaTemplate.send(topic, message).get();
    }
}
