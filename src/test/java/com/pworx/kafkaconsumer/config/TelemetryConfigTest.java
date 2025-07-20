package com.pworx.kafkaconsumer.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TelemetryConfigTest {

    @Test
    void beansAreCreated() {
        TelemetryConfig config = new TelemetryConfig();
        OpenTelemetry otel = config.openTelemetry();
        Meter meter = config.otelMeter(otel);
        assertNotNull(otel);
        assertNotNull(meter);
        ((OpenTelemetrySdk) otel).getSdkMeterProvider().close();
    }
}
