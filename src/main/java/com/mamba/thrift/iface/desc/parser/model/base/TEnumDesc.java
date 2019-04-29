package com.mamba.thrift.iface.desc.parser.model.base;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.thrift.TEnum;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@EqualsAndHashCode
public class TEnumDesc {

    private final String namespace;

    private final String name;

    private final List<Enumeration> enums;

    public TEnumDesc(Class<? extends TEnum> enumClass) throws Exception {
        this.namespace = enumClass.getPackage().getName();
        this.name = enumClass.getSimpleName();
        this.enums = Collections.unmodifiableList(Arrays.stream((TEnum[]) enumClass.getMethod("values").invoke(enumClass)).map(Enumeration::new).collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        if (this.getNamespace() == null || this.getNamespace().isEmpty()) {
            return String.format("{\"name\":\"%s\",\"enums\":%s}", this.getName(), this.getEnums());
        } else {
            return String.format("{\"namespace\":\"%s\",\"name\":\"%s\",\"enums\":%s}", this.getNamespace(), this.getName(), this.getEnums());
        }
    }

    @Getter
    @EqualsAndHashCode
    private static class Enumeration {

        private final String name;

        private final int value;

        public <E extends TEnum> Enumeration(E tEnum) {
            this.name = ((Enum) tEnum).name();
            this.value = tEnum.getValue();
        }

        @Override
        public String toString() {
            return String.format("{\"name\":\"%s\",\"value\":%d}", this.getName(), this.getValue());
        }
    }
}
