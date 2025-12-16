package org.example.model;

import java.io.Serializable;

public class VehicleAlert implements Serializable {
    private int alertId;
    private int vehicleId;
    private String type;
    private long timestamp;

    public VehicleAlert(int alertId, int vehicleId, String type, long timestamp) {
        this.alertId = alertId;
        this.vehicleId = vehicleId;
        this.type = type;
        this.timestamp = timestamp;
    }

    public VehicleAlert() {
    }

    public int getAlertId() {
        return alertId;
    }

    public void setAlertId(int alertId) {
        this.alertId = alertId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "VehicleAlert{" +
                "alertId=" + alertId +
                ", vehicleId=" + vehicleId +
                ", type='" + type + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
