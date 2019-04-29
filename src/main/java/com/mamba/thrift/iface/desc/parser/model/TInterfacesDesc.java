package com.mamba.thrift.iface.desc.parser.model;

import com.mamba.thrift.iface.desc.parser.model.base.TEnumDesc;
import com.mamba.thrift.iface.desc.parser.model.base.TStructDesc;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collection;
import java.util.Collections;

@Getter
@EqualsAndHashCode
public class TInterfacesDesc {

    private final Collection<TServiceDesc> services;

    private final Collection<TStructDesc> structs;

    private final Collection<TEnumDesc> enums;

    public TInterfacesDesc(Collection<TServiceDesc> services, Collection<TStructDesc> structs, Collection<TEnumDesc> enums) {
        this.services = Collections.unmodifiableCollection(services);
        this.structs = Collections.unmodifiableCollection(structs);
        this.enums = Collections.unmodifiableCollection(enums);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder().append('{');
        builder.append("\"services\":").append(this.getServices());
        if (!this.getStructs().isEmpty()) {
            builder.append(",\"structs\":").append(this.getStructs());
        }
        if (!this.getEnums().isEmpty()) {
            builder.append(",\"enums\":").append(this.getEnums());
        }
        return builder.append('}').toString();
    }
}
