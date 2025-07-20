package com.pworx.kafkaconsumer.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import static org.mockito.Mockito.*;

import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
class KafkaEventPublisherTest {

    @Mock
    KafkaTemplate<String, String> kafkaTemplate;

    @Test
    void publishSendsMessage() throws Exception {
        SendResult<String, String> result = new SendResult<>(null, null);
        var completableFuture = CompletableFuture.completedFuture(result);
        when(kafkaTemplate.send(anyString(), anyString())).thenReturn(completableFuture);
        KafkaEventPublisher publisher = new KafkaEventPublisher(kafkaTemplate);

        publisher.publish("topic", "msg");

        verify(kafkaTemplate).send("topic", "msg");
    }
}
