package com.mamba.thrift.iface.desc.parser.model.type;

import com.mamba.thrift.iface.desc.parser.enums.TType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.thrift.meta_data.FieldValueMetaData;

@Getter
@EqualsAndHashCode
public class TDataTypeDesc {

    private final String type;

    public TDataTypeDesc(TType type) {
        this.type = type.name().toLowerCase();
    }

    public TDataTypeDesc(FieldValueMetaData metaData) {
        this(TType.find(metaData.type, metaData.isBinary()));
    }

    @Override
    public String toString() {
        return String.format("{\"type\":\"%s\"}", this.getType());
    }
}
