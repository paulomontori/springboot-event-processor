package com.pworx.kafkaconsumer;

import com.pworx.kafkaconsumer.service.PurchaseService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaConsumerServiceTest {

    @Mock
    PurchaseService purchaseService;

    @InjectMocks
    KafkaConsumerService kafkaConsumerService;

    @Test
    void listenDelegatesToPurchaseService() {
        kafkaConsumerService.listen("msg");
        verify(purchaseService).process("msg");
    }

    @Test
    void exceptionFromPurchaseServicePropagates() {
        doThrow(new RuntimeException("fail")).when(purchaseService).process("x");
        assertThrows(RuntimeException.class, () -> kafkaConsumerService.listen("x"));
    }
}
