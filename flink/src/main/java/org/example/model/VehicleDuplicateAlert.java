package org.example.model;

import org.apache.flink.api.common.typeinfo.TypeInfo;
import org.example.type.VehicleDuplicateAlertTypeInfoFactory;

import java.io.Serializable;
import java.util.List;

@TypeInfo(VehicleDuplicateAlertTypeInfoFactory.class)
public class VehicleDuplicateAlert implements Serializable {
    List<Integer> duplicateAlertIds;
    int vehicleId;

    public VehicleDuplicateAlert(List<Integer> duplicateAlertIds, int vehicleId) {
        this.duplicateAlertIds = duplicateAlertIds;
        this.vehicleId = vehicleId;
    }

    public VehicleDuplicateAlert() {
    }

    public List<Integer> getDuplicateAlertIds() {
        return duplicateAlertIds;
    }

    public void setDuplicateAlertIds(List<Integer> duplicateAlertIds) {
        this.duplicateAlertIds = duplicateAlertIds;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    @Override
    public String toString() {
        return "VehicleDuplicateAlert{" +
                "duplicateAlertIds=" + duplicateAlertIds +
                ", vehicleId=" + vehicleId +
                '}';
    }
}
