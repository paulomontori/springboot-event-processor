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

/**
 * Configures OpenTelemetry for exporting custom metrics.  Separating telemetry
 * setup into its own configuration class allows metrics to be injected
 * anywhere without leaking vendor specific details throughout the codebase.
 */
@Configuration
public class TelemetryConfig {

    /**
     * Builds an {@link OpenTelemetry} instance with an OTLP metrics exporter.
     * The endpoint is read from the {@code OTEL_EXPORTER_OTLP_ENDPOINT}
     * environment variable so that deployments can route telemetry to different
     * backends.
     */
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

    /**
     * Exposes a {@link Meter} bean so other services can create counters and
     * gauges.  Using a named meter groups all metrics under a common namespace.
     */
    @Bean
    public Meter otelMeter(OpenTelemetry openTelemetry) {
        return openTelemetry.getMeter("springboot-event-processor");
    }
}
