# Spring Boot Event Processor

This project provides a basic Spring Boot application that consumes purchase
events from the `new_order` topic and breaks them into production orders. For
each item in a received purchase, a new event is emitted to
`assembly-line-topic` so that the production line can start processing it. Every
received purchase is also saved to a MongoDB collection so that orders can be
queried later.

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

MongoDB connection settings are also defined in that file. Before running the
application for the first time execute the initialization script:

```bash
mongo < scripts/mongo-init.js
```
