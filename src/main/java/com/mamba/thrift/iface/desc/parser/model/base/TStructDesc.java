package com.mamba.thrift.iface.desc.parser.model.base;

import com.mamba.thrift.iface.desc.parser.model.TFieldDesc;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.thrift.TBase;

import java.util.Collections;
import java.util.List;

@Getter
@EqualsAndHashCode
public class TStructDesc<T extends TFieldDesc> {

    private final String namespace;

    private final String name;

    private final List<T> fields;

    public TStructDesc(Class<? extends TBase> structClass, List<T> fields) {
        this.namespace = structClass.getPackage().getName();
        this.name = structClass.getSimpleName();
        this.fields = (fields == null || fields.isEmpty()) ? Collections.emptyList() : Collections.unmodifiableList(fields);
    }

    @Override
    public String toString() {
        if (this.getNamespace() == null || this.getNamespace().isEmpty()) {
            return String.format("{\"name\":\"%s\",\"fields\":%s}", this.getName(), this.getFields());
        } else {
            return String.format("{\"namespace\":\"%s\",\"name\":\"%s\",\"fields\":%s}", this.getNamespace(), this.getName(), this.getFields());
        }
    }
}
