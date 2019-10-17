package com.mamba.thrift.iface.desc.parser.model.type;

import com.mamba.thrift.iface.desc.parser.enums.TType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.thrift.TEnum;
import org.apache.thrift.meta_data.EnumMetaData;

@Getter
@EqualsAndHashCode
public class TEnumTypeDesc extends TDataTypeDesc {

    private final String enumRef;

    public TEnumTypeDesc(Class<? extends TEnum> enumClass) {
        super(TType.ENUM);
        this.enumRef = "$(" + enumClass.getName() + ")";
    }

    public TEnumTypeDesc(EnumMetaData metaData) {
        super(metaData);
        this.enumRef = "$(" + metaData.enumClass.getName() + ")";
    }

    @Override
    public String toString() {
        return String.format("{\"type\":\"%s\",\"enumRef\":\"%s\"}", this.getType(), this.getEnumRef());
    }
}