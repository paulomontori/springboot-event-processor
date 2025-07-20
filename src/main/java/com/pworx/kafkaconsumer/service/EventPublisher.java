package com.pworx.kafkaconsumer.service;

/**
 * Abstraction for publishing events to an external system.  Defining a simple
 * interface allows the domain layer to remain agnostic of the underlying
 * transport (Kafka in this case) and facilitates unit testing.
 */
public interface EventPublisher {

    /**
     * Publishes a message to the given topic.
     *
     * @param topic   destination topic name
     * @param message payload to send
     * @throws Exception if the message could not be delivered
     */
    void publish(String topic, String message) throws Exception;
}
