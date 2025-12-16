package org.example.model;

import java.io.Serializable;

public class EntityEvent implements Serializable {
    private int eventId;
    private int entityId;
    private String type;
    private long timestamp;

    public EntityEvent(int eventId, int entityId, String type, long timestamp) {
        this.eventId = eventId;
        this.entityId = entityId;
        this.type = type;
        this.timestamp = timestamp;
    }

    public EntityEvent() {
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
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
        return "EntityEvent{" +
                "eventId=" + eventId +
                ", entityId=" + entityId +
                ", type='" + type + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}

