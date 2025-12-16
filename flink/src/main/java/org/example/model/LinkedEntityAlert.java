package org.example.model;

import org.apache.flink.api.common.typeinfo.TypeInfo;
import org.example.type.LinkedEntityAlertTypeInfoFactory;

import java.io.Serializable;
import java.util.List;

@TypeInfo(LinkedEntityAlertTypeInfoFactory.class)
public class LinkedEntityAlert implements Serializable {
    List<Integer> linkedEventIds;
    int entityId;

    public LinkedEntityAlert(List<Integer> linkedEventIds, int entityId) {
        this.linkedEventIds = linkedEventIds;
        this.entityId = entityId;
    }

    public LinkedEntityAlert() {
    }

    public List<Integer> getLinkedEventIds() {
        return linkedEventIds;
    }

    public void setLinkedEventIds(List<Integer> linkedEventIds) {
        this.linkedEventIds = linkedEventIds;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    @Override
    public String toString() {
        return "LinkedEntityAlert{" +
                "linkedEventIds=" + linkedEventIds +
                ", entityId=" + entityId +
                '}';
    }
}

