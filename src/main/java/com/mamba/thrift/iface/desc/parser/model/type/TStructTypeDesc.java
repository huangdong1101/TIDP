package com.mamba.thrift.iface.desc.parser.model.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.thrift.meta_data.StructMetaData;

@Getter
@EqualsAndHashCode
public class TStructTypeDesc extends TDataTypeDesc {

    private final String structRef;

    public TStructTypeDesc(StructMetaData metaData) {
        super(metaData);
        this.structRef = "$(" + metaData.structClass.getName() + ")";
    }

    @Override
    public String toString() {
        return String.format("{\"type\":\"%s\",\"structRef\":\"%s\"}", this.getType(), this.getStructRef());
    }
}