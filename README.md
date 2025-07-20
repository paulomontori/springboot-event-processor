# Spring Boot Event Processor

This application consumes purchase events from Kafka, stores them in MongoDB and
emits one production event per item. Metrics are exported through OpenTelemetry
using the OTLP protocol so Grafana or other observability tools can monitor the
processing pipeline.

## Architecture

```mermaid
graph TD
    A[(new_order topic)] --> B[KafkaConsumerService]
    B --> C[PurchaseService]
    C --> D[(MongoDB)]
    C -->|for each item| E[KafkaEventPublisher]
    E --> F[(assembly-line-topic)]
    C --> G[OpenTelemetry]
    G --> H[(OTLP Collector)]
```

## Build

```bash
mvn package
```

Run the application with:

```bash
mvn spring-boot:run
```

Before the first run initialise MongoDB:

```bash
mongo < scripts/mongo-init.js
```

Metrics are exported to the OTLP endpoint defined by the `OTEL_EXPORTER_OTLP_ENDPOINT`
environment variable (defaults to `http://localhost:4317`). Connection properties
for Kafka and MongoDB are defined in `src/main/resources/application.properties`.

## Continuous Integration

GitHub Actions build the project and run unit tests with JaCoCo code coverage.
The generated report is uploaded as a workflow artifact.

## Dependencies

The following Maven dependencies are used in this project:

- **spring-boot-starter-web** – provides the base Spring Boot web stack used by
  the embedded server, although this project exposes no controllers it pulls in
  common Boot auto configuration.
- **spring-kafka** – integrates Kafka clients with Spring, simplifying consumer
  and producer configuration.
- **spring-retry** – used to transparently retry the consumer method when
  transient failures occur.
- **spring-boot-starter-data-mongodb** – supplies Spring Data repositories for
  persisting purchase documents.
- **spring-boot-starter-aop** – required by Spring Retry for method interception
  and is otherwise unused directly.
- **lombok** – optional compile-time annotation processor used only for tests in
  this project; included to reduce boilerplate when needed.
- **spring-boot-starter-test** – brings in JUnit and assertion libraries for the
  unit tests.
- **opentelemetry-api**, **opentelemetry-sdk** and **opentelemetry-exporter-otlp**
  – provide metric instrumentation and export to an OTLP endpoint for
  observability.
- **spring-boot-maven-plugin** – allows building and running the application via
  Maven goals.
- **jacoco-maven-plugin** – generates test coverage reports during the build.
