package com.mamba.thrift.iface.desc.parser.model;

import com.mamba.thrift.iface.desc.parser.model.type.TDataTypeDesc;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
@EqualsAndHashCode
public class TMethodDesc {

    private final String method;

    private final List<TFieldDesc> params;

    private final TDataTypeDesc result;

    public TMethodDesc(String method, List<TFieldDesc> params, TDataTypeDesc result) {
        this.method = method;
        this.params = (params == null || params.isEmpty()) ? Collections.emptyList() : Collections.unmodifiableList(params);
        this.result = result;
    }

    @Override
    public String toString() {
        if (this.getResult() == null) {
            return String.format("{\"method\":\"%s\",\"params\":%s}", this.getMethod(), this.getParams());
        } else {
            return String.format("{\"method\":\"%s\",\"params\":%s,\"result\":%s}", this.getMethod(), this.getParams(), this.getResult());
        }
    }
}
