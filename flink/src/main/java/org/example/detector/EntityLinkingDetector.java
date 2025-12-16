package org.example.detector;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.example.model.EntityEvent;
import org.example.model.LinkedEntityAlert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EntityLinkingDetector extends KeyedProcessFunction<Integer, EntityEvent, LinkedEntityAlert> {

    private transient ValueState<List<Integer>> eventIds;
    private static final Logger log = LoggerFactory.getLogger(EntityLinkingDetector.class);

    @Override
    public void open(Configuration parameters) throws Exception {
        ValueStateDescriptor<List<Integer>> eventIdsDescriptor = new ValueStateDescriptor<>("seen", Types.LIST(Types.INT));
        eventIds = getRuntimeContext().getState(eventIdsDescriptor);
    }

    @Override
    public void processElement(EntityEvent entityEvent, KeyedProcessFunction<Integer, EntityEvent, LinkedEntityAlert>.Context context, Collector<LinkedEntityAlert> collector) throws Exception {
        log.info("Incoming Entity Event {}!", entityEvent.getEventId());
        List<Integer> currentEventIds = eventIds.value();

        if (currentEventIds == null) {
            currentEventIds = new ArrayList<>();
        }

        if (currentEventIds.size() > 0) {
            currentEventIds.add(entityEvent.getEventId());
            LinkedEntityAlert linkedEntityAlert = new LinkedEntityAlert(new ArrayList<>(currentEventIds), entityEvent.getEntityId());
            collector.collect(linkedEntityAlert);
        } else {
            currentEventIds.add(entityEvent.getEventId());
        }

        eventIds.update(currentEventIds);
    }
}

