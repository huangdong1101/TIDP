package com.mamba.thrift.iface.desc.parser.model.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.thrift.TBase;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.MapMetaData;

import java.util.function.BiFunction;

@Getter
@EqualsAndHashCode
public class TMapTypeDesc<K extends TDataTypeDesc, V extends TDataTypeDesc> extends TDataTypeDesc {

    private final K key;

    private final V value;

    public static TMapTypeDesc create(Class<? extends TBase> structClass, MapMetaData metaData, BiFunction<Class<? extends TBase>, FieldValueMetaData, TDataTypeDesc> function) {
        return new TMapTypeDesc(metaData, function.apply(structClass, metaData.keyMetaData), function.apply(structClass, metaData.valueMetaData));
    }

    private TMapTypeDesc(MapMetaData metaData, K key, V value) {
        super(metaData);
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("{\"type\":\"%s\",\"key\":%s,\"value\":%s}", this.getType(), this.getKey(), this.getValue());
    }
}

