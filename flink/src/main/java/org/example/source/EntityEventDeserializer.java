package org.example.source;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.EntityEvent;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class EntityEventDeserializer implements DeserializationSchema<EntityEvent> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public EntityEvent deserialize(byte[] bytes) throws IOException {
        return mapper.readValue(bytes, EntityEvent.class);
    }

    @Override
    public boolean isEndOfStream(EntityEvent entityEvent) {
        return false;
    }

    @Override
    public TypeInformation<EntityEvent> getProducedType() {
        return TypeInformation.of(EntityEvent.class);
    }
}

