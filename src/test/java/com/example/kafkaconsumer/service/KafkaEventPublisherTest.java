package com.example.kafkaconsumer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaEventPublisherTest {

    @Mock
    KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void publishSendsMessage() throws Exception {
        SettableListenableFuture<SendResult<String, String>> future = spy(new SettableListenableFuture<>());
        future.set(null);
        when(kafkaTemplate.send("topic", "msg")).thenReturn(future);
        KafkaEventPublisher publisher = new KafkaEventPublisher(kafkaTemplate);

        publisher.publish("topic", "msg");

        verify(kafkaTemplate).send("topic", "msg");
        verify(future).get();
    }
}
