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

    public TEnumDesc(Class<? extends TEnum> enumClass) {
        Package pkg = enumClass.getPackage();
        this.namespace = (pkg == null) ? null : pkg.getName();
        this.name = enumClass.getSimpleName();
        this.enums = Collections.unmodifiableList(Arrays.stream(getValues(enumClass)).map(Enumeration::new).collect(Collectors.toList()));
    }

    @Override
    public String toString() {
        if (this.getNamespace() == null || this.getNamespace().isEmpty()) {
            return String.format("{\"name\":\"%s\",\"enums\":%s}", this.getName(), this.getEnums());
        } else {
            return String.format("{\"namespace\":\"%s\",\"name\":\"%s\",\"enums\":%s}", this.getNamespace(), this.getName(), this.getEnums());
        }
    }

    private static TEnum[] getValues(Class<? extends TEnum> enumClass) {
        try {
            return (TEnum[]) enumClass.getMethod("values").invoke(enumClass);
        } catch (Exception e) {
            throw new IllegalStateException("Failed invoke method 'values' of '" + enumClass.getName() + "': " + e.getMessage(), e);
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
