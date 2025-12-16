package org.example.detector;

import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.util.Collector;
import org.example.model.VehicleAlert;
import org.example.model.VehicleDuplicateAlert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class VehicleDuplicateDetector extends KeyedProcessFunction<Integer, VehicleAlert, VehicleDuplicateAlert> {

    private transient ValueState<List<Integer>> alertIds;
    private static final Logger log = LoggerFactory.getLogger(VehicleDuplicateDetector.class);

    @Override
    public void open(Configuration parameters) throws Exception {
        ValueStateDescriptor<List<Integer>> alertIdsDescriptor = new ValueStateDescriptor<>("seen", Types.LIST(Types.INT));
        alertIds = getRuntimeContext().getState(alertIdsDescriptor);
    }

    @Override
    public void processElement(VehicleAlert vehicleAlert, KeyedProcessFunction<Integer, VehicleAlert, VehicleDuplicateAlert>.Context context, Collector<VehicleDuplicateAlert> collector) throws Exception {
        log.info("Incoming Vehicle Alert {}!", vehicleAlert.getAlertId());
        List<Integer> currentAlertIds = alertIds.value();

        if (currentAlertIds == null) {
            currentAlertIds = new ArrayList<>();
        }

        if (currentAlertIds.size() > 0) {
            currentAlertIds.add(vehicleAlert.getAlertId());
            VehicleDuplicateAlert vehicleDuplicateAlert = new VehicleDuplicateAlert(new ArrayList<>(currentAlertIds), vehicleAlert.getVehicleId());
            collector.collect(vehicleDuplicateAlert);
        } else {
            currentAlertIds.add(vehicleAlert.getAlertId());
        }

        alertIds.update(currentAlertIds);
    }
}
