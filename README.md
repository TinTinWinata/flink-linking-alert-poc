# Flink Vehicle Duplicate Detection POC

This is a Proof of Concept (POC) demonstrating how to use Apache Flink for real-time duplicate vehicle alert detection. The application processes vehicle alerts from RabbitMQ, detects duplicates for the same vehicle, and sends duplicate alerts to an HTTP endpoint.

## Overview

The application implements a streaming pipeline that:
1. **Consumes** vehicle alerts from a RabbitMQ queue
2. **Processes** alerts by vehicle ID to detect duplicates using Flink's stateful processing
3. **Sends** duplicate alerts to an HTTP endpoint

## Architecture

```
RabbitMQ Queue (vehicle-alerts)
    ↓
Flink Source (VehicleAlertDeserializer)
    ↓
Keyed Stream (by vehicleId)
    ↓
Stateful Processing (VehicleDuplicateDetector)
    ↓
HTTP Sink (VehicleDuplicateHttpSink)
    ↓
HTTP Endpoint (http://localhost:5000/test)
```

## Components

### 1. **VehicleDuplicateJob**
The main Flink job that orchestrates the streaming pipeline:
- Configures RabbitMQ connection
- Sets up the data stream with keying by `vehicleId`
- Applies duplicate detection logic
- Sends results to HTTP sink

### 2. **VehicleDuplicateDetector**
A `KeyedProcessFunction` that maintains state to track alert IDs per vehicle:
- Uses Flink's `ValueState` to store seen alert IDs for each vehicle
- Detects duplicates when a second alert for the same vehicle arrives
- Emits `VehicleDuplicateAlert` when duplicates are found

### 3. **Models**
- **VehicleAlert**: Input model containing `alertId`, `vehicleId`, `type`, and `timestamp`
- **VehicleDuplicateAlert**: Output model containing a list of duplicate alert IDs and the vehicle ID

### 4. **Source & Sink**
- **VehicleAlertDeserializer**: Deserializes JSON messages from RabbitMQ into `VehicleAlert` objects
- **VehicleDuplicateHttpSink**: Sends duplicate alerts as JSON to an HTTP endpoint

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
java -cp target/classes:target/dependency/* org.example.VehicleDuplicateJob
```

Or if you have a Flink cluster:

```bash
flink run -c org.example.VehicleDuplicateJob target/flink-linking-detection-1.0-SNAPSHOT.jar
```

## Configuration

Key configuration constants in `VehicleDuplicateJob.java`:

- `RABBITMQ_HOST`: RabbitMQ host (default: `localhost`)
- `RABBITMQ_PORT`: RabbitMQ port (default: `5672`)
- `RABBITMQ_QUEUE`: Queue name (default: `vehicle-alerts`)
- HTTP endpoint in `VehicleDuplicateHttpSink.java`: `http://localhost:5000/test`

## Usage

### Sending Test Messages

You can send test messages to RabbitMQ using the management UI or command line:

**Using RabbitMQ Management UI:**
1. Navigate to `http://localhost:15672`
2. Login with `guest`/`guest`
3. Go to Queues → `vehicle-alerts` → Publish message
4. Send JSON messages like:

```json
{
  "alertId": 1,
  "vehicleId": 100,
  "type": "SPEEDING",
  "timestamp": 1699123456000
}
```

**Using RabbitMQ CLI:**
```bash
docker exec -it rabbitmq rabbitmqadmin publish exchange=amq.default routing_key=vehicle-alerts payload='{"alertId":1,"vehicleId":100,"type":"SPEEDING","timestamp":1699123456000}'
```

### Expected Behavior

1. **First alert** for a vehicle: Stored in state, no output
2. **Second alert** for the same vehicle: Triggers a `VehicleDuplicateAlert` containing both alert IDs
3. **Subsequent alerts** for the same vehicle: Each new alert triggers a duplicate alert with all accumulated alert IDs

Example output:
```json
{
  "duplicateAlertIds": [1, 2],
  "vehicleId": 100
}
```

## Technology Stack

- **Apache Flink 1.18.1**: Stream processing framework
- **RabbitMQ**: Message queue for vehicle alerts
- **Jackson**: JSON serialization/deserialization
- **Apache HTTP Client 5**: HTTP sink implementation
- **Java 17**: Programming language

## Project Structure

```
flink-linking-detection/
├── flink/
│   ├── src/main/java/org/example/
│   │   ├── VehicleDuplicateJob.java          # Main Flink job
│   │   ├── detector/
│   │   │   └── VehicleDuplicateDetector.java # Duplicate detection logic
│   │   ├── model/
│   │   │   ├── VehicleAlert.java              # Input model
│   │   │   └── VehicleDuplicateAlert.java     # Output model
│   │   ├── source/
│   │   │   └── VehicleAlertDeserializer.java  # RabbitMQ deserializer
│   │   ├── sink/
│   │   │   └── VehicleDuplicateHttpSink.java # HTTP sink
│   │   └── type/
│   │       └── VehicleDuplicateAlertTypeInfoFactory.java
│   └── pom.xml
└── rabbitmq/
    └── docker-compose.yml                     # RabbitMQ setup
```

## License

This is a POC project for demonstration purposes.

