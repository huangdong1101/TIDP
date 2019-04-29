package com.mamba.thrift.iface.desc.parser.model.type;

import com.mamba.thrift.iface.desc.parser.enums.TType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.thrift.meta_data.FieldValueMetaData;

@Getter
@EqualsAndHashCode
public class TDataTypeDesc {

    private final String type;

    public TDataTypeDesc(FieldValueMetaData metaData) {
        this.type = TType.find(metaData.type, metaData.isBinary()).name().toLowerCase();
    }

    @Override
    public String toString() {
        return String.format("{\"type\":\"%s\"}", this.getType());
    }
}
