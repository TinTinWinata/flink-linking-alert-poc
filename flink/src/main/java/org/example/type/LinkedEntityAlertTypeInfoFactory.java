package org.example.type;

import org.apache.flink.api.common.typeinfo.TypeInfoFactory;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.common.typeinfo.Types;
import org.apache.flink.api.java.typeutils.PojoField;
import org.apache.flink.api.java.typeutils.PojoTypeInfo;
import org.example.model.LinkedEntityAlert;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LinkedEntityAlertTypeInfoFactory extends TypeInfoFactory<LinkedEntityAlert> {

    @Override
    public TypeInformation<LinkedEntityAlert> createTypeInfo(Type t, Map<String, TypeInformation<?>> genericParameters) {
        try {
            List<PojoField> fields = new ArrayList<>();
            fields.add(new PojoField(
                    LinkedEntityAlert.class.getDeclaredField("linkedEventIds"),
                    Types.LIST(Types.INT)
            ));
            fields.add(new PojoField(
                    LinkedEntityAlert.class.getDeclaredField("entityId"),
                    Types.INT
            ));
            return new PojoTypeInfo<>(LinkedEntityAlert.class, fields);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException("Failed to create type information for LinkedEntityAlert", e);
        }
    }
}

