package com.mamba.thrift.iface.desc.parser.model.type;

import com.mamba.thrift.iface.desc.parser.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.thrift.meta_data.FieldValueMetaData;
import org.apache.thrift.meta_data.MapMetaData;

@Getter
@EqualsAndHashCode
public class TMapTypeDesc<K extends TDataTypeDesc, V extends TDataTypeDesc> extends TDataTypeDesc {

    private final K key;

    private final V value;

    public static TMapTypeDesc create(MapMetaData metaData, Function<FieldValueMetaData, TDataTypeDesc> function) throws Exception {
        return new TMapTypeDesc(metaData, function.apply(metaData.keyMetaData), function.apply(metaData.valueMetaData));
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

