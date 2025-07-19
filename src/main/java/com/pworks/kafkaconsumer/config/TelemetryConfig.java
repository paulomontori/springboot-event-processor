package com.pworks.kafkaconsumer.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.exporter.prometheus.PrometheusHttpServer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelemetryConfig {

    @Bean
    public OpenTelemetry openTelemetry() {
        SdkMeterProvider meterProvider = SdkMeterProvider.builder()
                .registerMetricReader(PrometheusHttpServer.builder()
                        .setHost("0.0.0.0")
                        .setPort(9464)
                        .build())
                .build();

        return OpenTelemetrySdk.builder()
                .setMeterProvider(meterProvider)
                .build();
    }

    @Bean
    public Meter otelMeter(OpenTelemetry openTelemetry) {
        return openTelemetry.getMeter("springboot-event-processor");
    }
}
