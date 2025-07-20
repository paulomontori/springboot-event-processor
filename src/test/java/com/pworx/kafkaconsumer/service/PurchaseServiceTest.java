package com.pworx.kafkaconsumer.service;

import com.pworx.kafkaconsumer.model.Purchase;
import com.pworx.kafkaconsumer.repository.PurchaseRepository;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongCounterBuilder;
import io.opentelemetry.api.metrics.Meter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @Mock
    EventPublisher eventPublisher;
    @Mock
    PurchaseRepository purchaseRepository;
    @Mock
    Meter meter;
    @Mock
    LongCounterBuilder longCounterBuilder;
    @Mock
    LongCounter longCounter;

    PurchaseService purchaseService;

    @BeforeEach
    void setUp() {
        when(meter.counterBuilder(anyString())).thenReturn(longCounterBuilder);
        when(longCounterBuilder.setDescription(anyString())).thenReturn(longCounterBuilder);
        when(longCounterBuilder.build()).thenReturn(longCounter);
        purchaseService = new PurchaseService(eventPublisher, purchaseRepository, meter);
    }

    @Test
    void processPublishesEventsAndSavesPurchase() throws Exception {
        String json = "{\"eventTimestamp\":\"2024-01-01\",\"payload\":{\"purchaseId\":\"p1\",\"items\":[{\"productId\":\"A\",\"quantity\":1},{\"productId\":\"B\",\"quantity\":2}]}}";

        purchaseService.process(json);

        verify(eventPublisher, times(2)).publish(eq("assembly-line-topic"), anyString());
        verify(purchaseRepository).save(any(Purchase.class));
        verify(longCounter).add(1);
    }

    @Test
    void invalidJsonThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> purchaseService.process("not-json"));
    }

    @Test
    void publishFailureThrowsEventPublishException() throws Exception {
        String json = "{\"eventTimestamp\":\"2024-01-01\",\"payload\":{\"purchaseId\":\"p1\",\"items\":[{\"productId\":\"A\",\"quantity\":1}]}}";
        doThrow(new Exception("fail")).when(eventPublisher).publish(anyString(), anyString());

        assertThrows(EventPublishException.class, () -> purchaseService.process(json));
    }
}
