package com.mamba.thrift.iface.desc.parser.model.type;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.thrift.meta_data.EnumMetaData;

@Getter
@EqualsAndHashCode
public class TEnumTypeDesc extends TDataTypeDesc {

    private final String enumRef;

    public TEnumTypeDesc(EnumMetaData metaData) {
        super(metaData);
        this.enumRef = "$(" + metaData.enumClass.getName() + ")";
    }

    @Override
    public String toString() {
        return String.format("{\"type\":\"%s\",\"enumRef\":\"%s\"}", this.getType(), this.getEnumRef());
    }
}