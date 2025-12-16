package org.example.type;

import org.apache.flink.api.common.typeinfo.TypeInfoFactory;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.PojoField;
import org.apache.flink.api.java.typeutils.PojoTypeInfo;
import org.example.model.VehicleDuplicateAlert;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VehicleDuplicateAlertTypeInfoFactory extends TypeInfoFactory<VehicleDuplicateAlert> {

    @Override
    public TypeInformation<VehicleDuplicateAlert> createTypeInfo(Type t, Map<String, TypeInformation<?>> genericParameters) {
        try {
            List<PojoField> fields = new ArrayList<>();
            fields.add(new PojoField(
                    VehicleDuplicateAlert.class.getDeclaredField("duplicateAlertIds"),
                    Types.LIST(Types.INT)
            ));
            fields.add(new PojoField(
                    VehicleDuplicateAlert.class.getDeclaredField("vehicleId"),
                    Types.INT
            ));
            return new PojoTypeInfo<>(VehicleDuplicateAlert.class, fields);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to create type information for VehicleDuplicateAlert", e);
        }
    }
}

