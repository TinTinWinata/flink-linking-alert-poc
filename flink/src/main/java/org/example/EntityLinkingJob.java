package org.example;

import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.rabbitmq.RMQSource;
import org.apache.flink.streaming.connectors.rabbitmq.common.RMQConnectionConfig;
import org.example.detector.EntityLinkingDetector;
import org.example.model.EntityEvent;
import org.example.model.LinkedEntityAlert;
import org.example.sink.LinkedEntityHttpSink;
import org.example.source.EntityEventDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityLinkingJob {

    private static final Logger log = LoggerFactory.getLogger(EntityLinkingJob.class);

    // RabbitMQ Configuration
    private static final String RABBITMQ_HOST = "localhost";
    private static final int RABBITMQ_PORT = 5672;
    private static final String RABBITMQ_QUEUE = "entity-events";

    // Others Configuration
    private static final long TIME_WINDOW_SECONDS = 60;

    public static void main(String[] args) {
        log.info("Starting Entity Linking Job...");

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        RMQConnectionConfig connectionConfig = new RMQConnectionConfig.Builder()
                .setHost(RABBITMQ_HOST)
                .setPort(RABBITMQ_PORT)
                .setUserName("guest")
                .setPassword("guest")
                .setVirtualHost("/")
                .build();

        RMQSource<EntityEvent> rmqSource = new RMQSource<>(connectionConfig, RABBITMQ_QUEUE, true, new EntityEventDeserializer());

        DataStream<EntityEvent> entityEventStream = env.addSource(rmqSource);

        DataStream<LinkedEntityAlert> linkedEntityAlertStream = entityEventStream
                .keyBy(EntityEvent::getEntityId)
                .process(new EntityLinkingDetector())
                .returns(TypeInformation.of(LinkedEntityAlert.class));

        linkedEntityAlertStream.addSink(new LinkedEntityHttpSink());

        try {
            env.execute("Entity Linking");
        } catch(Exception ex) {
            log.warn("Entity Linking Job Failed to Execute {}", ex.getMessage());
        }

        log.info("Entity Linking Job Finish!");
    }
}

