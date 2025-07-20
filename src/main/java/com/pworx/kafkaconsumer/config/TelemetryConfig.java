package com.pworx.kafkaconsumer.config;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.metrics.SdkMeterProvider;
import io.opentelemetry.exporter.otlp.metrics.OtlpGrpcMetricExporter;
import io.opentelemetry.sdk.metrics.export.MetricExporter;
import io.opentelemetry.sdk.metrics.export.PeriodicMetricReader;
import java.time.Duration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TelemetryConfig {

    @Bean
    public OpenTelemetry openTelemetry() {
        MetricExporter metricExporter = OtlpGrpcMetricExporter.builder()
                .setEndpoint(System.getenv().getOrDefault("OTEL_EXPORTER_OTLP_ENDPOINT", "http://localhost:4317"))
                .build();

        SdkMeterProvider meterProvider = SdkMeterProvider.builder()
                .registerMetricReader(PeriodicMetricReader.builder(metricExporter)
                        .setInterval(Duration.ofSeconds(30))
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
