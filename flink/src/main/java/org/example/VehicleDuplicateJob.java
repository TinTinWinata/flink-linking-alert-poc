package org.example;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.example.detector.VehicleDuplicateDetector;
import org.example.model.VehicleAlert;
import org.example.model.VehicleDuplicateAlert;
import org.example.sink.VehicleDuplicateHttpSink;
import org.example.source.VehicleAlertDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VehicleDuplicateJob {

    private static final Logger log = LoggerFactory.getLogger(VehicleDuplicateJob.class);

    // RabbitMQ Configuration
    private static final String RABBITMQ_HOST = "localhost";
    private static final int RABBITMQ_PORT = 5672;
    private static final String RABBITMQ_QUEUE = "vehicle-alerts";

    // Others Configuration
    private static final long TIME_WINDOW_SECONDS = 60;

    public static void main(String[] args) {
        log.info("Starting Vehicle Linking Alert Job...");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost(RABBITMQ_HOST)
                .setPort(RABBITMQ_PORT)
                .setUserName("guest")
                .setPassword("guest")
                .setVirtualHost("/")
                .build();

        RMQSource<VehicleAlert> rmqSource = new RMQSource<>(connectionConfig, RABBITMQ_QUEUE, true, new VehicleAlertDeserializer());

        DataStream<VehicleAlert> vehicleAlertStream = env.addSource(rmqSource);

        DataStream<VehicleDuplicateAlert> vehicleDuplicateAlertStream = vehicleAlertStream
                .keyBy(VehicleAlert::getVehicleId)
                .process(new VehicleDuplicateDetector())
                .returns(TypeInformation.of(VehicleDuplicateAlert.class));

        vehicleDuplicateAlertStream.addSink(new VehicleDuplicateHttpSink());

        try {
            env.execute("Vehicle");
        } catch(Exception ex) {
            log.warn("Vehicle Linking Alert Job Failed to Execute {}", ex.getMessage());
        }

        log.info("Vehicle Linking Alert Job Finish!");
    }
}