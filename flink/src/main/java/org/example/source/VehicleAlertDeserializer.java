package org.example.source;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.VehicleAlert;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

public class VehicleAlertDeserializer implements DeserializationSchema<VehicleAlert> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public VehicleAlert deserialize(byte[] bytes) throws IOException {
        return mapper.readValue(bytes, VehicleAlert.class);
    }

    @Override
    public boolean isEndOfStream(VehicleAlert vehicleAlert) {
        return false;
    }

    @Override
    public TypeInformation<VehicleAlert> getProducedType() {
        return TypeInformation.of(VehicleAlert.class);
    }
}
