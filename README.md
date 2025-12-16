# Flink Entity Linking Detection

This is a Proof of Concept (POC) demonstrating how to use Apache Flink for real-time entity linking detection. The application processes entity events from RabbitMQ, detects linked events for the same entity, and sends linked entity alerts to an HTTP endpoint.

## Overview

The application implements a streaming pipeline that:
1. **Consumes** entity events from a RabbitMQ queue
2. **Processes** events by entity ID to detect linked events using Flink's stateful processing
3. **Sends** linked entity alerts to an HTTP endpoint

## Architecture

```
RabbitMQ Queue (entity-events)
    ↓
Flink Source (EntityEventDeserializer)
    ↓
Keyed Stream (by entityId)
    ↓
Stateful Processing (EntityLinkingDetector)
    ↓
HTTP Sink (LinkedEntityHttpSink)
    ↓
HTTP Endpoint (http://localhost:5000/test)
```

## Components

### 1. **EntityLinkingJob**
The main Flink job that orchestrates the streaming pipeline:
- Configures RabbitMQ connection
- Sets up the data stream with keying by `entityId`
- Applies entity linking logic
- Sends results to HTTP sink

### 2. **EntityLinkingDetector**
A `KeyedProcessFunction` that maintains state to track event IDs per entity:
- Uses Flink's `ValueState` to store seen event IDs for each entity
- Detects linked events when a second event for the same entity arrives
- Emits `LinkedEntityAlert` when linked events are found

### 3. **Models**
- **EntityEvent**: Input model containing `eventId`, `entityId`, `type`, and `timestamp`
- **LinkedEntityAlert**: Output model containing a list of linked event IDs and the entity ID

### 4. **Source & Sink**
- **EntityEventDeserializer**: Deserializes JSON messages from RabbitMQ into `EntityEvent` objects
- **LinkedEntityHttpSink**: Sends linked entity alerts as JSON to an HTTP endpoint

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker and Docker Compose (for RabbitMQ)

## Setup

### 1. Start RabbitMQ

```bash
cd rabbitmq
docker-compose up -d
```

RabbitMQ will be available at:
- AMQP: `localhost:5672`
- Management UI: `http://localhost:15672` (guest/guest)

### 2. Build the Project

```bash
cd flink
mvn clean package
```

### 3. Run the Flink Job

```bash
java -cp target/classes:target/dependency/* org.example.EntityLinkingJob
```

Or if you have a Flink cluster:

```bash
flink run -c org.example.EntityLinkingJob target/flink-linking-detection-1.0-SNAPSHOT.jar
```

## Configuration

Key configuration constants in `EntityLinkingJob.java`:

- `RABBITMQ_HOST`: RabbitMQ host (default: `localhost`)
- `RABBITMQ_PORT`: RabbitMQ port (default: `5672`)
- `RABBITMQ_QUEUE`: Queue name (default: `entity-events`)
- HTTP endpoint in `LinkedEntityHttpSink.java`: `http://localhost:5000/test`

## Usage

### Sending Test Messages

You can send test messages to RabbitMQ using the management UI or command line:

**Using RabbitMQ Management UI:**
1. Navigate to `http://localhost:15672`
2. Login with `guest`/`guest`
3. Go to Queues → `entity-events` → Publish message
4. Send JSON messages like:

```json
{
  "eventId": 1,
  "entityId": 100,
  "type": "EVENT_TYPE",
  "timestamp": 1699123456000
}
```

**Using RabbitMQ CLI:**
```bash
docker exec -it rabbitmq rabbitmqadmin publish exchange=amq.default routing_key=entity-events payload='{"eventId":1,"entityId":100,"type":"EVENT_TYPE","timestamp":1699123456000}'
```

### Expected Behavior

1. **First event** for an entity: Stored in state, no output
2. **Second event** for the same entity: Triggers a `LinkedEntityAlert` containing both event IDs
3. **Subsequent events** for the same entity: Each new event triggers a linked entity alert with all accumulated event IDs

Example output:
```json
{
  "linkedEventIds": [1, 2],
  "entityId": 100
}
```

## Technology Stack

- **Apache Flink 1.18.1**: Stream processing framework
- **RabbitMQ**: Message queue for entity events
- **Jackson**: JSON serialization/deserialization
- **Apache HTTP Client 5**: HTTP sink implementation
- **Java 17**: Programming language

## Project Structure

```
flink-linking-detection/
├── flink/
│   ├── src/main/java/org/example/
│   │   ├── EntityLinkingJob.java          # Main Flink job
│   │   ├── detector/
│   │   │   └── EntityLinkingDetector.java # Entity linking logic
│   │   ├── model/
│   │   │   ├── EntityEvent.java              # Input model
│   │   │   └── LinkedEntityAlert.java     # Output model
│   │   ├── source/
│   │   │   └── EntityEventDeserializer.java  # RabbitMQ deserializer
│   │   ├── sink/
│   │   │   └── LinkedEntityHttpSink.java # HTTP sink
│   │   └── type/
│   │       └── LinkedEntityAlertTypeInfoFactory.java
│   └── pom.xml
└── rabbitmq/
    └── docker-compose.yml                     # RabbitMQ setup
```

## License

This is a POC project for demonstration purposes.
