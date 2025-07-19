# Spring Boot Event Processor

This project provides a basic Spring Boot application that consumes Kafka events
and propagates new ones. When a purchase event is consumed from `example-topic`,
the application emits a copy of that event to `assembly-line-topic` and
`inventory-topic`.

## Build

```bash
mvn package
```

Run the application with:

```bash
mvn spring-boot:run
```

The Kafka bootstrap server location and serialization settings can be adjusted in
`src/main/resources/application.properties`.
