package com.mamba.thrift.iface.desc.parser.model;

import com.mamba.thrift.iface.desc.parser.enums.TRequirement;
import com.mamba.thrift.iface.desc.parser.model.type.TDataTypeDesc;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class TFieldDesc {

    private final String name;

    private final String requirement;

    private final TDataTypeDesc dataType;

    public TFieldDesc(String name, byte requirement, TDataTypeDesc dataType) {
        this.name = name;
        this.requirement = TRequirement.findByValue(requirement).name().toLowerCase();
        this.dataType = dataType;
    }

    @Override
    public String toString() {
        return String.format("{\"name\":\"%s\",\"requirement\":\"%s\",\"dataType\":%s}", this.getName(), this.getRequirement(), this.getDataType());
    }
}
